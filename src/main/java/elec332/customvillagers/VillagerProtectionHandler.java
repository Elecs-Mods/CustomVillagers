package elec332.customvillagers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.ArrayList;

/**
 * Created by Elec332 on 2-3-2015.
 */
public class VillagerProtectionHandler {
    public VillagerProtectionHandler(boolean all, ArrayList<Integer> IDS){
        if (!all)
            this.IDS = IDS;
    }

    ArrayList<Integer> IDS;

    @SubscribeEvent
    public void VillagerProtection(LivingHurtEvent event){
        if (event.entity.getClass().isAssignableFrom(EntityVillager.class)){
            EntityVillager villager = (EntityVillager)event.entity;
            if (IDS.contains(villager.getProfession()) || IDS == null){
                event.setCanceled(true);
            }
        }
    }
}
