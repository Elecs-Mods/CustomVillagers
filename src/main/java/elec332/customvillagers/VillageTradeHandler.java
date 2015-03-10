package elec332.customvillagers;

import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Elec332 on 12-2-2015.
 */
public class VillageTradeHandler implements VillagerRegistry.IVillageTradeHandler {

    public VillageTradeHandler(int ID, ArrayList<MerchantRecipe> array){
        super();
        this.ID = ID;
        this.trades = array;
    }

    int ID;
    ArrayList<MerchantRecipe> trades;

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
