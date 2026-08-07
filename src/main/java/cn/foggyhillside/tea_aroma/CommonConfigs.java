package cn.foggyhillside.tea_aroma;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfigs {
    public static ModConfigSpec SPEC;
    public static ModConfigSpec.IntValue BAMBOO_TRAY_MAX_PROGRESS;

    static {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        BUILDER.push("Configs for Tea Aroma");

        BAMBOO_TRAY_MAX_PROGRESS = BUILDER.comment("Default: 600").defineInRange("bamboo_tray_processing_time", 600, 10, 1000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
