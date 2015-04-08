package elec332.customvillagers;

import elec332.core.baseclasses.item.BaseItem;
import elec332.core.main.ElecCTab;
import elec332.customvillagers.main.CustomVillagerModContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

/**
 * Created by Elec332 on 15-2-2015.
 */
public class DebugItem extends BaseItem {
    public DebugItem(){
        super("Debugger", ElecCTab.ElecTab, CustomVillagerModContainer.instance.modID());
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (!player.worldObj.isRemote) {
            if (entity instanceof EntityVillager) {
                player.addChatMessage(new ChatComponentTranslation("Villager ID is: " + ((EntityVillager) entity).getProfession()));
                return true;
            }
        }
        return false;
    }
}
