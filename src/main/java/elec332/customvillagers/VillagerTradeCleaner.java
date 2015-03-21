package elec332.customvillagers;

import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipeList;

import java.util.Random;

/**
 * Created by Elec332 on 20-3-2015.
 */
public class VillagerTradeCleaner implements VillagerRegistry.IVillageTradeHandler {

    public VillagerTradeCleaner(int i){
        this.ID = i;
    }

    private int ID;
    @Override
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
        if (villager.getProfession() == ID) {
            recipeList.clear();
        }
    }
}
