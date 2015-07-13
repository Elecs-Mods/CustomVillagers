package elec332.customvillagers.entity;

import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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

import java.util.*;


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
    private float field_82191_bN;           //Dafuq?

    public EntityCustomVillager(World world) {
        super(world);
    }


    public void onTransformed(){
    }

    public EntityCustomVillager(World p_i1748_1_, int p_i1748_2_) {
        super(p_i1748_1_);
        this.setProfession(p_i1748_2_);
        this.setSize(0.6F, 1.8F);
        this.getNavigator().setBreakDoors(true);
        this.getNavigator().setAvoidsWater(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.tasks.addTask(1, new EntityAITradePlayer(this));
        this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
        this.tasks.addTask(2, new EntityAIMoveIndoors(this));
        this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(4, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(6, new EntityAIVillagerMate(this));
        this.tasks.addTask(7, new EntityAIFollowGolem(this));
        this.tasks.addTask(8, new EntityAIPlay(this, 0.32D));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F));
        this.tasks.addTask(9, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
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
    public boolean interact(EntityPlayer p_70085_1_) {
        ItemStack itemstack = p_70085_1_.inventory.getCurrentItem();
        boolean flag = itemstack != null && itemstack.getItem() == Items.spawn_egg;

        if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !p_70085_1_.isSneaking()) {
            if (!this.worldObj.isRemote) {
                this.setCustomer(p_70085_1_);
                p_70085_1_.displayGUIMerchant(this, this.getCustomNameTag());
            }

            return true;
        } else {
            return super.interact(p_70085_1_);
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, 0);
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

    private float adjustProbability(float p_82188_1_) {
        float f1 = p_82188_1_ + this.field_82191_bN;
        return f1 > 0.9F ? 0.9F - (f1 - 0.9F) : f1;
    }

    /**
     * based on the villagers profession add items, equipment, and recipies adds par1 random items to the list of things
     * that the villager wants to buy. (at most 1 of each wanted type is added)
     */
    private void addDefaultEquipmentAndRecipies(int p_70950_1_) {
        if (this.buyingList != null) {
            this.field_82191_bN = MathHelper.sqrt_float((float) this.buyingList.size()) * 0.2F;
        } else {
            this.field_82191_bN = 0.0F;
        }

        MerchantRecipeList merchantrecipelist;
        merchantrecipelist = new MerchantRecipeList();
        VillagerRegistry.manageVillagerTrades(merchantrecipelist, this, this.getProfession(), this.rand);
        int k;
        label50:

        switch (this.getProfession()) {
            case 0:
                func_146091_a(merchantrecipelist, Items.wheat, this.rand, this.adjustProbability(0.9F));
                func_146091_a(merchantrecipelist, Item.getItemFromBlock(Blocks.wool), this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.chicken, this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.cooked_fished, this.rand, this.adjustProbability(0.4F));
                func_146089_b(merchantrecipelist, Items.bread, this.rand, this.adjustProbability(0.9F));
                func_146089_b(merchantrecipelist, Items.melon, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.apple, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.cookie, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.shears, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.flint_and_steel, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.cooked_chicken, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.arrow, this.rand, this.adjustProbability(0.5F));

                if (this.rand.nextFloat() < this.adjustProbability(0.5F)) {
                    merchantrecipelist.add(new MerchantRecipe(new ItemStack(Blocks.gravel, 10), new ItemStack(Items.emerald), new ItemStack(Items.flint, 4 + this.rand.nextInt(2), 0)));
                }

                break;
            case 1:
                func_146091_a(merchantrecipelist, Items.paper, this.rand, this.adjustProbability(0.8F));
                func_146091_a(merchantrecipelist, Items.book, this.rand, this.adjustProbability(0.8F));
                func_146091_a(merchantrecipelist, Items.written_book, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Item.getItemFromBlock(Blocks.bookshelf), this.rand, this.adjustProbability(0.8F));
                func_146089_b(merchantrecipelist, Item.getItemFromBlock(Blocks.glass), this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.compass, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.clock, this.rand, this.adjustProbability(0.2F));

                if (this.rand.nextFloat() < this.adjustProbability(0.07F)) {
                    Enchantment enchantment = Enchantment.enchantmentsBookList[this.rand.nextInt(Enchantment.enchantmentsBookList.length)];
                    int i1 = MathHelper.getRandomIntegerInRange(this.rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
                    ItemStack itemstack = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment, i1));
                    k = 2 + this.rand.nextInt(5 + i1 * 10) + 3 * i1;
                    merchantrecipelist.add(new MerchantRecipe(new ItemStack(Items.book), new ItemStack(Items.emerald, k), itemstack));
                }

                break;
            case 2:
                func_146089_b(merchantrecipelist, Items.ender_eye, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.experience_bottle, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.redstone, this.rand, this.adjustProbability(0.4F));
                func_146089_b(merchantrecipelist, Item.getItemFromBlock(Blocks.glowstone), this.rand, this.adjustProbability(0.3F));
                Item[] aitem = new Item[]{Items.iron_sword, Items.diamond_sword, Items.iron_chestplate, Items.diamond_chestplate, Items.iron_axe, Items.diamond_axe, Items.iron_pickaxe, Items.diamond_pickaxe};
                Item[] aitem1 = aitem;
                int j = aitem.length;
                k = 0;

                while (true) {
                    if (k >= j) {
                        break label50;
                    }

                    Item item = aitem1[k];

                    if (this.rand.nextFloat() < this.adjustProbability(0.05F)) {
                        merchantrecipelist.add(new MerchantRecipe(new ItemStack(item, 1, 0), new ItemStack(Items.emerald, 2 + this.rand.nextInt(3), 0), EnchantmentHelper.addRandomEnchantment(this.rand, new ItemStack(item, 1, 0), 5 + this.rand.nextInt(15))));
                    }

                    ++k;
                }
            case 3:
                func_146091_a(merchantrecipelist, Items.coal, this.rand, this.adjustProbability(0.7F));
                func_146091_a(merchantrecipelist, Items.iron_ingot, this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.gold_ingot, this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.diamond, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.iron_sword, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.diamond_sword, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.iron_axe, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.diamond_axe, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.iron_pickaxe, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.diamond_pickaxe, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.iron_shovel, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_shovel, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_hoe, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_hoe, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_boots, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_boots, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_helmet, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_helmet, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_chestplate, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_chestplate, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.iron_leggings, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.diamond_leggings, this.rand, this.adjustProbability(0.2F));
                func_146089_b(merchantrecipelist, Items.chainmail_boots, this.rand, this.adjustProbability(0.1F));
                func_146089_b(merchantrecipelist, Items.chainmail_helmet, this.rand, this.adjustProbability(0.1F));
                func_146089_b(merchantrecipelist, Items.chainmail_chestplate, this.rand, this.adjustProbability(0.1F));
                func_146089_b(merchantrecipelist, Items.chainmail_leggings, this.rand, this.adjustProbability(0.1F));
                break;
            case 4:
                func_146091_a(merchantrecipelist, Items.coal, this.rand, this.adjustProbability(0.7F));
                func_146091_a(merchantrecipelist, Items.porkchop, this.rand, this.adjustProbability(0.5F));
                func_146091_a(merchantrecipelist, Items.beef, this.rand, this.adjustProbability(0.5F));
                func_146089_b(merchantrecipelist, Items.saddle, this.rand, this.adjustProbability(0.1F));
                func_146089_b(merchantrecipelist, Items.leather_chestplate, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.leather_boots, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.leather_helmet, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.leather_leggings, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.cooked_porkchop, this.rand, this.adjustProbability(0.3F));
                func_146089_b(merchantrecipelist, Items.cooked_beef, this.rand, this.adjustProbability(0.3F));
        }

        if (merchantrecipelist.isEmpty()) {
            func_146091_a(merchantrecipelist, Items.gold_ingot, this.rand, 1.0F);
        }

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

    public static void func_146091_a(MerchantRecipeList p_146091_0_, Item p_146091_1_, Random p_146091_2_, float p_146091_3_) {
        if (p_146091_2_.nextFloat() < p_146091_3_) {
            p_146091_0_.add(new MerchantRecipe(func_146088_a(p_146091_1_, p_146091_2_), Items.emerald));
        }
    }

    private static ItemStack func_146088_a(Item p_146088_0_, Random p_146088_1_) {
        return new ItemStack(p_146088_0_, func_146092_b(p_146088_0_, p_146088_1_), 0);
    }

    private static int func_146092_b(Item p_146092_0_, Random p_146092_1_) {
        Tuple tuple = (Tuple) villagersSellingList.get(p_146092_0_);
        return tuple == null ? 1 : (((Integer) tuple.getFirst()).intValue() >= ((Integer) tuple.getSecond()).intValue() ? ((Integer) tuple.getFirst()).intValue() : ((Integer) tuple.getFirst()).intValue() + p_146092_1_.nextInt(((Integer) tuple.getSecond()).intValue() - ((Integer) tuple.getFirst()).intValue()));
    }

    public static void func_146089_b(MerchantRecipeList p_146089_0_, Item p_146089_1_, Random p_146089_2_, float p_146089_3_) {
        if (p_146089_2_.nextFloat() < p_146089_3_) {
            int i = func_146090_c(p_146089_1_, p_146089_2_);
            ItemStack itemstack;
            ItemStack itemstack1;

            if (i < 0) {
                itemstack = new ItemStack(Items.emerald, 1, 0);
                itemstack1 = new ItemStack(p_146089_1_, -i, 0);
            } else {
                itemstack = new ItemStack(Items.emerald, i, 0);
                itemstack1 = new ItemStack(p_146089_1_, 1, 0);
            }

            p_146089_0_.add(new MerchantRecipe(itemstack, itemstack1));
        }
    }

    private static int func_146090_c(Item p_146090_0_, Random p_146090_1_) {
        Tuple tuple = (Tuple) blacksmithSellingList.get(p_146090_0_);
        return tuple == null ? 1 : (((Integer) tuple.getFirst()).intValue() >= ((Integer) tuple.getSecond()).intValue() ? ((Integer) tuple.getFirst()).intValue() : ((Integer) tuple.getFirst()).intValue() + p_146090_1_.nextInt(((Integer) tuple.getSecond()).intValue() - ((Integer) tuple.getFirst()).intValue()));
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
        VillagerRegistry.applyRandomTrade(this, worldObj.rand);
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