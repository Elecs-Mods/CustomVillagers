package elec332.customvillagers.gui;

import elec332.core.util.BasicInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class ContainerVillagerGUI extends Container{
    public ContainerVillagerGUI(int ID, EntityPlayer player){
        this.ID = ID;
        this.inventory = new BasicInventory("RecipeMaker", 3);
        this.player = player;
        this.addSlotToContainer(new Slot(inventory, 0, 36, 53));
        this.addSlotToContainer(new Slot(inventory, 1, 62, 53));
        this.addSlotToContainer(new Slot(inventory, 2, 120, 53));
        int i;

        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
    }

    protected EntityPlayer player;

    @Override
    public ItemStack slotClick(int slotID, int i1, int i2, EntityPlayer player) {
        ItemStack ret;
        if (slotID >= 0 && slotID < 3){
            ret = getSlot(slotID).getStack();
            getSlot(slotID).putStack(copyItemStack(player.inventory.getItemStack()));
        } else return super.slotClick(slotID, i1, i2, player);
        return ret;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        if (slotID >= 0 && slotID < 3)
            getSlot(slotID).putStack(null);
        return null;  //No fancy stuff needed
    }

    @Override
    protected void retrySlotClick(int p_75133_1_, int p_75133_2_, boolean p_75133_3_, EntityPlayer p_75133_4_) {

    }

    private ItemStack copyItemStack(ItemStack stack){
        if (stack == null)
            return null;
        ItemStack stack1 = stack.copy();
        stack1.stackSize = stack.stackSize;
        return stack1;
    }

    public BasicInventory inventory;
    protected int ID;

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
