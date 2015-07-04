package elec332.customvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.customvillagers.gui.ContainerVillagerGUI;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 3-7-2015.
 */
public class PacketSyncTradeContents extends AbstractPacket {

    public PacketSyncTradeContents(){
        networkPackageObject = new NBTTagCompound();
    }

    public PacketSyncTradeContents(ItemStack s1, ItemStack s2, ItemStack o){
        NBTTagCompound tagCompound = new NBTTagCompound();
        if (s1 != null)
            tagCompound.setTag("s1", s1.writeToNBT(new NBTTagCompound()));
        if (s2 != null)
            tagCompound.setTag("s2", s2.writeToNBT(new NBTTagCompound()));
        if (o != null)
            tagCompound.setTag("o", o.writeToNBT(new NBTTagCompound()));
        networkPackageObject = tagCompound;
    }

    @Override
    public IMessage onMessage(AbstractPacket abstractPacket, MessageContext messageContext) {
        Container container = messageContext.getServerHandler().playerEntity.openContainer;
        if (container instanceof ContainerVillagerGUI) {
            ((ContainerVillagerGUI)container).inventory.setInventorySlotContents(0, ItemStack.loadItemStackFromNBT(abstractPacket.networkPackageObject.getCompoundTag("s1")));
            ((ContainerVillagerGUI)container).inventory.setInventorySlotContents(1, ItemStack.loadItemStackFromNBT(abstractPacket.networkPackageObject.getCompoundTag("s2")));
            ((ContainerVillagerGUI)container).inventory.setInventorySlotContents(2, ItemStack.loadItemStackFromNBT(abstractPacket.networkPackageObject.getCompoundTag("o")));
        }
        return null;
    }
}
