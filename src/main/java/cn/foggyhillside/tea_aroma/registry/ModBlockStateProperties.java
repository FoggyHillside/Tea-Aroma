package cn.foggyhillside.tea_aroma.registry;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockStateProperties {
    public static final IntegerProperty AMOUNT = IntegerProperty.create("amount", 0, 3);
    public static final IntegerProperty PROCESS_TYPE = IntegerProperty.create("process_type", 0, 3);
    public static final BooleanProperty WITH_SUGAR = BooleanProperty.create("with_sugar");
    public static final BooleanProperty WITH_HONEY = BooleanProperty.create("with_honey");
    public static final BooleanProperty WITH_CINNAMON = BooleanProperty.create("with_cinnamon");
}