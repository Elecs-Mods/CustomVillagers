package elec332.customvillagers.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import elec332.customvillagers.entity.EntityCustomVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Created by Elec332 on 13-2-2015.
 */
public class VillagerTransformer {

    @SubscribeEvent
    public void VillagerTransformerHandler(EntityJoinWorldEvent event){
        if (event.entity.getClass().isAssignableFrom(EntityVillager.class)){
            event.setCanceled(true);
            NBTTagCompound tag = new NBTTagCompound();
            event.entity.writeToNBT(tag);
            event.world.removeEntity(event.entity);
            EntityCustomVillager entityCustomVillager = new EntityCustomVillager(event.world);
            entityCustomVillager.readFromNBT(tag);
            entityCustomVillager.onTransformed();
            event.world.spawnEntityInWorld(entityCustomVillager);
        }
    }

}
