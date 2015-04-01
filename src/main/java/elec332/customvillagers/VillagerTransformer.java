package elec332.customvillagers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Created by Elec332 on 13-2-2015.
 */
public class VillagerTransformer {

    public VillagerTransformer(int ID, int customProfession, Float chance){
        this.ID = ID;
        this.profession = customProfession;
        this.chance = chance;
    }

    int ID;
    int profession;
    Float chance;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void VillagerTransformerHandler(EntityJoinWorldEvent event){
        if (event.entity.getClass().isAssignableFrom(EntityVillager.class)){
            if (((EntityVillager)event.entity).getProfession() == ID) {
                if (event.entity.worldObj.rand.nextFloat() < chance)
                    ((EntityVillager) event.entity).setProfession(profession);
            }
        }
    }
}
