package elec332.customvillagers.event;

import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 12-2-2015.
 */
public class VillageTradeHandler implements VillagerRegistry.IVillageTradeHandler {

    public VillageTradeHandler(int ID, List<MerchantRecipe> array){
        super();
        this.ID = ID;
        this.trades = array;
    }

    int ID;
    List<MerchantRecipe> trades;

    @Override
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
        if (villager.getProfession() == ID) {
            //recipeList.clear();
            for (MerchantRecipe r : trades) {
                recipeList.addToListWithCheck(r);
            }
        }
    }
}
