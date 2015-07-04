package elec332.customvillagers.proxies;

import cpw.mods.fml.common.network.IGuiHandler;
import elec332.customvillagers.gui.ContainerVillagerGUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 12-2-2015.
 */
public abstract class CommonProxy implements IGuiHandler{

    public void registerResourcePacks(){
    }

    public void registerVillagerSkins(int i, ResourceLocation resourceLocation) {
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerVillagerGUI(ID, player);
    }
}
