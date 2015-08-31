package elec332.customvillagers.proxies;

import cpw.mods.fml.common.registry.VillagerRegistry;
import elec332.core.inventory.BaseContainer;
import elec332.customvillagers.CustomVillagers;
import elec332.customvillagers.client.TextureHandler;
import elec332.customvillagers.gui.ClientGuiVillager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Elec332 on 12-2-2015.
 */
public class ClientProxy extends CommonProxy {
    @Override
    @SuppressWarnings("unchecked")
    public void registerResourcePacks() {
        try {
            Field f = Minecraft.class.getDeclaredField(CustomVillagers.getDeclaredFieldDefaultResourcePacks());
            f.setAccessible(true);
            List list = (List) f.get(Minecraft.getMinecraft());
            list.add(new TextureHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerVillagerSkin(int i, ResourceLocation resourceLocation) {
        VillagerRegistry.instance().registerVillagerSkin(i, resourceLocation);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ClientGuiVillager((BaseContainer) getServerGuiElement(ID, player, world, x, y, z));
    }
}
