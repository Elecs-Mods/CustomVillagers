package elec332.customvillagers.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.customvillagers.json.Algorithm;
import elec332.customvillagers.json.VillagerData;
import elec332.customvillagers.registry.CustomVillagerRegistry;
import elec332.customvillagers.util.CorrectedTuple;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Random;


/**
 * Created by Elec332 on 13-7-2015.
 */
public class EntityCustomVillager extends EntityVillager {
    private int randomTickDivider;
    private boolean isMating;               //????                                                          -Oh, wait, this is used for breeding
    private boolean isPlaying;              //Playing? Guitar? With his favourite toy cars?                 -Oh, this is used for doing the talky stuff
    private Village villageObj;
    private EntityPlayer buyingPlayer;
    private MerchantRecipeList buyingList;
    private int timeUntilReset;
    private boolean needsInitilization;
    private int wealth;                     //Interesting, this only gets updated, but it's never used
    private String lastBuyingPlayer;
    private boolean isLookingForHome;
    private float probabilityStuff;           //Dafuq?


    public EntityCustomVillager(World world) {
        super(world);
    }

    public void onTransformed(){
        setProfession(CustomVillagerRegistry.instance.getNewID(this));
    }

    @Override
    protected void updateAITick() {
        if (--this.randomTickDivider <= 0) {
            this.worldObj.villageCollectionObj.addVillagerPosition(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
            this.randomTickDivider = 70 + this.rand.nextInt(50);
            this.villageObj = this.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 32);
            if (this.villageObj == null) {
                this.detachHome();
            } else {
                ChunkCoordinates chunkcoordinates = this.villageObj.getCenter();
                this.setHomeArea(chunkcoordinates.posX, chunkcoordinates.posY, chunkcoordinates.posZ, (int) ((float) this.villageObj.getVillageRadius() * 0.6F));

                if (this.isLookingForHome) {
                    this.isLookingForHome = false;
                    this.villageObj.setDefaultPlayerReputation(5);
                }
            }
        }

        if (!this.isTrading() && this.timeUntilReset > 0) {
            --this.timeUntilReset;

            if (this.timeUntilReset <= 0) {
                if (this.needsInitilization) {
                    if (this.buyingList.size() > 1) {
                        for (Object obj : this.buyingList){
                            MerchantRecipe merchantRecipe = (MerchantRecipe) obj;
                            if (merchantRecipe.isRecipeDisabled()) {
                                merchantRecipe.func_82783_a(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                            }
                        }
                    }

                    this.addDefaultEquipmentAndRecipies(1);
                    this.needsInitilization = false;

                    if (this.villageObj != null && this.lastBuyingPlayer != null) {
                        this.worldObj.setEntityState(this, (byte) 14);
                        this.villageObj.setReputationForPlayer(this.lastBuyingPlayer, 1);
                    }
                }

                this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
            }
        }

        super.updateAITick();
    }

    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack itemstack = player.inventory.getCurrentItem();
        boolean flag = itemstack != null && itemstack.getItem() == Items.spawn_egg;

        if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking()) {
            if (!this.worldObj.isRemote) {
                this.setCustomer(player);
                player.displayGUIMerchant(this, this.getCustomNameTag());
            }

            return true;
        } else {
            return super.interact(player);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("Profession", this.getProfession());
        tag.setInteger("Riches", this.wealth);
        if (this.buyingList != null) {
            tag.setTag("Offers", this.buyingList.getRecipiesAsTags());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setProfession(tag.getInteger("Profession"));
        this.wealth = tag.getInteger("Riches");
        if (tag.hasKey("Offers", 10)) {
            NBTTagCompound trades = tag.getCompoundTag("Offers");
            this.buyingList = new MerchantRecipeList(trades);
        }
    }

    @Override
    public boolean isMating() {
        return this.isMating;
    }

    @Override
    public void setMating(boolean b) {
        this.isMating = b;
    }

    @Override
    public void setPlaying(boolean b) {
        this.isPlaying = b;
    }

    @Override
    public boolean isPlaying() {
        return this.isPlaying;
    }

    @Override
    public void setRevengeTarget(EntityLivingBase revengeTarget) {
        super.setRevengeTarget(revengeTarget);
        if (this.villageObj != null && revengeTarget != null) {
            this.villageObj.addOrRenewAgressor(revengeTarget);
            if (revengeTarget instanceof EntityPlayer) {
                byte b0 = -1;
                if (this.isChild()) {
                    b0 = -3;
                }
                this.villageObj.setReputationForPlayer(revengeTarget.getCommandSenderName(), b0);
                if (this.isEntityAlive()) {
                    this.worldObj.setEntityState(this, (byte) 13);
                }
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        if (this.villageObj != null) {
            Entity entity = damageSource.getEntity();
            if (entity != null) {
                if (entity instanceof EntityPlayer) {
                    this.villageObj.setReputationForPlayer(entity.getCommandSenderName(), -2);
                } else if (entity instanceof IMob) {
                    this.villageObj.endMatingSeason();
                }
            } else {
                EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
                if (entityplayer != null) {
                    this.villageObj.endMatingSeason();
                }
            }
        }

        super.onDeath(damageSource);
    }

    @Override
    public void setCustomer(EntityPlayer player) {
        this.buyingPlayer = player;
    }

    @Override
    public EntityPlayer getCustomer() {
        return this.buyingPlayer;
    }

    @Override
    public boolean isTrading() {
        return this.buyingPlayer != null;
    }

    @Override
    public void useRecipe(MerchantRecipe trade) {
        trade.incrementToolUses();
        this.livingSoundTime = -this.getTalkInterval();
        this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
        if (trade.hasSameIDsAs((MerchantRecipe) this.buyingList.get(this.buyingList.size() - 1))) {
            this.timeUntilReset = 40;
            this.needsInitilization = true;
            if (this.buyingPlayer != null) {
                this.lastBuyingPlayer = this.buyingPlayer.getCommandSenderName();
            } else {
                this.lastBuyingPlayer = null;
            }
        }
        if (trade.getItemToBuy().getItem() == Items.emerald) {
            this.wealth += trade.getItemToBuy().stackSize;
        }
    }

    @Override
    public void func_110297_a_(ItemStack stack) {
        if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval();
            if (stack != null) {
                this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
            } else {
                this.playSound("mob.villager.no", this.getSoundVolume(), this.getSoundPitch());
            }
        }
    }

    @Override
    public MerchantRecipeList getRecipes(EntityPlayer p_70934_1_) {
        if (this.buyingList == null) {
            this.addDefaultEquipmentAndRecipies(1);
        }
        return this.buyingList;
    }

    public float adjustProbability(float f) {
        float f1 = f + this.probabilityStuff;
        return f1 > 0.9F ? 0.9F - (f1 - 0.9F) : f1;
    }

    /**
     * based on the villagers profession add items, equipment, and recipies adds par1 random items to the list of things
     * that the villager wants to buy. (at most 1 of each wanted type is added)
     */
    @SuppressWarnings("unchecked")
    private void addDefaultEquipmentAndRecipies(int p_70950_1_) {
        if (this.buyingList != null) {
            this.probabilityStuff = MathHelper.sqrt_float((float) this.buyingList.size()) * 0.2F;
        } else {
            this.probabilityStuff = 0.0F;
        }
        VillagerData data = CustomVillagerRegistry.instance.getData(getProfession());

        MerchantRecipeList merchantrecipelist = new MerchantRecipeList();
        //TODO: VillagerRegistry.manageVillagerTrades(merchantrecipelist, this, this.getProfession(), this.rand);

        merchantrecipelist.addAll(data.getTrades(this));

        if (merchantrecipelist.isEmpty()) {
            merchantrecipelist.add(new MerchantRecipe(new ItemStack(Items.gold_ingot, Algorithm.calculateFromTuple(fromVillagerSellingList(Items.gold_ingot))), Items.emerald));
        }

        if (data.shuffle)
            Collections.shuffle(merchantrecipelist);

        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        for (int l = 0; l < p_70950_1_ && l < merchantrecipelist.size(); ++l) {
            this.buyingList.addToListWithCheck((MerchantRecipe) merchantrecipelist.get(l));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setRecipes(MerchantRecipeList trades) {
    }

    public static CorrectedTuple<Integer, Integer> fromVillagerSellingList(Item item){
        Tuple tuple = (Tuple)villagersSellingList.get(item);
        if (tuple != null)
            return CorrectedTuple.fromTuple(tuple);
        return null;
    }

    public static CorrectedTuple<Integer, Integer> fromBlackSmithSellingList(Item item){
        Tuple tuple = (Tuple)blacksmithSellingList.get(item);
        if (tuple != null)
            return CorrectedTuple.fromTuple(tuple);
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 12) {
            this.generateRandomParticles("heart");
        } else if (b == 13) {
            this.generateRandomParticles("angryVillager");
        } else if (b == 14) {
            this.generateRandomParticles("happyVillager");
        } else {
            super.handleHealthUpdate(b);
        }
    }

    @Override
    public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
        data = super.onSpawnWithEgg(data);
        cpw.mods.fml.common.registry.VillagerRegistry.applyRandomTrade(this, worldObj.rand);
        return data;
    }

    @SideOnly(Side.CLIENT)
    private void generateRandomParticles(String p_70942_1_) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(p_70942_1_, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + 1.0D + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
        }
    }

    @Override
    public void setLookingForHome() {
        this.isLookingForHome = true;
    }

    @Override
    public EntityVillager createChild(EntityAgeable p_90011_1_) {
        EntityCustomVillager entityVillager = new EntityCustomVillager(this.worldObj);
        entityVillager.onSpawnWithEgg(null);
        return entityVillager;
    }

}