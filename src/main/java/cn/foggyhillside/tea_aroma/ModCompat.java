package cn.foggyhillside.tea_aroma;

import net.neoforged.fml.ModList;

public class ModCompat {
    public static boolean isFestivalDelicaciesLoaded() {
        return ModList.get().isLoaded("festival_delicacies");
    }
}