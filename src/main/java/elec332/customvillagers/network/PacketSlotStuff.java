package elec332.customvillagers.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import elec332.core.network.AbstractPacket;
import elec332.core.util.NBTHelper;
import elec332.customvillagers.gui.ContainerVillagerGUI;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 23-8-2015.
 */
public class PacketSlotStuff extends AbstractPacket {

    public PacketSlotStuff(){
    }

    public PacketSlotStuff(int i, boolean b){
        super(new NBTHelper().addToTag(i, "i").addToTag(b, "b").toNBT());
    }

    @Override
    public IMessage onMessage(AbstractPacket message, MessageContext ctx) {
        Container container = ctx.getServerHandler().playerEntity.openContainer;
        if (container instanceof ContainerVillagerGUI) {
            ((ContainerVillagerGUI) container).slot(message.networkPackageObject.getInteger("i"), message.networkPackageObject.getBoolean("b"));
        }
        return null;
    }

}
