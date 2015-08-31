package elec332.customvillagers.util;

import com.google.common.base.Strings;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

/**
 * Created by Elec332 on 15-7-2015.
 */
public class Util {

    public static Item fromString(String s){
        if (Strings.isNullOrEmpty(s))
            return null;
        String[] split = s.replace(":", " ").split(" ");
        return GameRegistry.findItem(split[0], split[1]);
    }

}
