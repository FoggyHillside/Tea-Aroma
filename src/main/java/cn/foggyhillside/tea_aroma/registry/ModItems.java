package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.FoodList;
import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.items.KettleItem;
import cn.foggyhillside.tea_aroma.items.TeaItem;
import cn.foggyhillside.tea_aroma.items.TeaTooltipItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, TeaAroma.MODID);

    private static final String DRYING = TeaAroma.MODID + ".tooltip.drying";
    private static final String ROLLING = TeaAroma.MODID + ".tooltip.rolling";
    private static final String FERMENTATION = TeaAroma.MODID + ".tooltip.fermentation";
    private static final String WITHERING = TeaAroma.MODID + ".tooltip.withering";
    private static final String SCENTING = TeaAroma.MODID + ".tooltip.scenting";

    private static Item.Properties tea() {
        return new Item.Properties().stacksTo(1).craftRemainder(ModItems.CUP.get());
    }

    public static final Supplier<Item> KETTLE = ITEMS.register("kettle",
            () -> new KettleItem(ModBlocks.KETTLE.get(), new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> CUP = ITEMS.register("cup",
            () -> new BlockItem(ModBlocks.CUP.get(), new Item.Properties().stacksTo(16)));
    public static final Supplier<Item> BAMBOO_TRAY = ITEMS.register("bamboo_tray",
            () -> new BlockItem(ModBlocks.BAMBOO_TRAY.get(), new Item.Properties()));

    public static final Supplier<Item> TEA_SAPLING = ITEMS.register("tea_sapling",
            () -> new BlockItem(ModBlocks.TEA_TREE.get(), new Item.Properties()));
    public static final Supplier<Item> BAMBOO_LEAVES = registerTeaInProcessing("bamboo_leaves",
            List.of(), List.of(DRYING));
    public static final Supplier<Item> FRESH_TEA_LEAVES = registerTeaInProcessing("fresh_tea_leaves",
            List.of(), List.of(DRYING, WITHERING));
    //Tea Leaves
    public static final Supplier<Item> BAMBOO_TEA_LEAVES = ITEMS.register("bamboo_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> WHITE_TEA_LEAVES = ITEMS.register("white_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> GREEN_TEA_LEAVES = ITEMS.register("green_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BLACK_TEA_LEAVES = ITEMS.register("black_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> YELLOW_TEA_LEAVES = ITEMS.register("yellow_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> OOLONG_TEA_LEAVES = ITEMS.register("oolong_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DARK_TEA_LEAVES = ITEMS.register("dark_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> ROSE_TEA_LEAVES = ITEMS.register("rose_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> DANDELION_TEA_LEAVES = ITEMS.register("dandelion_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> LILAC_TEA_LEAVES = ITEMS.register("lilac_tea_leaves",
            () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BLUE_ORCHID_TEA_LEAVES = ITEMS.register("blue_orchid_tea_leaves",
            () -> new Item(new Item.Properties()));
    //Tea In Processing
    public static final Supplier<Item> ROSE_TEA_IN_PROCESSING = registerTeaInProcessing("rose_tea_in_processing",
            List.of(DRYING, SCENTING), List.of(DRYING));
    public static final Supplier<Item> DANDELION_TEA_IN_PROCESSING = registerTeaInProcessing("dandelion_tea_in_processing",
            List.of(DRYING, SCENTING), List.of(DRYING));
    public static final Supplier<Item> LILAC_TEA_IN_PROCESSING = registerTeaInProcessing("lilac_tea_in_processing",
            List.of(DRYING, SCENTING), List.of(DRYING));
    public static final Supplier<Item> BLUE_ORCHID_TEA_IN_PROCESSING = registerTeaInProcessing("blue_orchid_tea_in_processing",
            List.of(DRYING, SCENTING), List.of(DRYING));
    public static final Supplier<Item> TEA_IN_PROCESSING_0 = registerTeaInProcessing("tea_in_processing_0",
            List.of(DRYING), List.of(ROLLING, SCENTING));
    public static final Supplier<Item> TEA_IN_PROCESSING_0_0 = registerTeaInProcessing("tea_in_processing_0_0",
            List.of(DRYING, ROLLING), List.of(DRYING, FERMENTATION));
    public static final Supplier<Item> TEA_IN_PROCESSING_0_0_0 = registerTeaInProcessing("tea_in_processing_0_0_0",
            List.of(DRYING, ROLLING, FERMENTATION), List.of(DRYING, ROLLING));
    public static final Supplier<Item> TEA_IN_PROCESSING_1 = registerTeaInProcessing("tea_in_processing_1",
            List.of(WITHERING), List.of(DRYING, ROLLING));
    public static final Supplier<Item> TEA_IN_PROCESSING_1_0 = registerTeaInProcessing("tea_in_processing_1_0",
            List.of(WITHERING, ROLLING), List.of(FERMENTATION, DRYING));
    public static final Supplier<Item> DARK_TEA_IN_PROCESSING = registerTeaInProcessing("dark_tea_in_processing",
            List.of(DRYING, ROLLING, FERMENTATION, ROLLING), List.of(DRYING));
    public static final Supplier<Item> BLACK_TEA_IN_PROCESSING = registerTeaInProcessing("black_tea_in_processing",
            List.of(WITHERING, ROLLING, FERMENTATION), List.of(DRYING));
    public static final Supplier<Item> OOLONG_TEA_IN_PROCESSING_0 = registerTeaInProcessing("oolong_tea_in_processing_0",
            List.of(WITHERING, ROLLING, DRYING), List.of(ROLLING));
    public static final Supplier<Item> OOLONG_TEA_IN_PROCESSING_1 = registerTeaInProcessing("oolong_tea_in_processing_1",
            List.of(WITHERING, ROLLING, DRYING, ROLLING), List.of(DRYING));
    //Tea
    public static final Supplier<Item> BAMBOO_TEA = ITEMS.register("bamboo_tea",
            () -> new TeaItem(ModBlocks.BAMBOO_TEA.get(), tea().food(FoodList.BAMBOO_TEA)));
    public static final Supplier<Item> WHITE_TEA = ITEMS.register("white_tea",
            () -> new TeaItem(ModBlocks.WHITE_TEA.get(), tea().food(FoodList.WHITE_TEA)));
    public static final Supplier<Item> GREEN_TEA = ITEMS.register("green_tea",
            () -> new TeaItem(ModBlocks.GREEN_TEA.get(), tea().food(FoodList.GREEN_TEA)));
    public static final Supplier<Item> BLACK_TEA = ITEMS.register("black_tea",
            () -> new TeaItem(ModBlocks.BLACK_TEA.get(), tea().food(FoodList.BLACK_TEA)));
    public static final Supplier<Item> YELLOW_TEA = ITEMS.register("yellow_tea",
            () -> new TeaItem(ModBlocks.YELLOW_TEA.get(), tea().food(FoodList.YELLOW_TEA)));
    public static final Supplier<Item> OOLONG_TEA = ITEMS.register("oolong_tea",
            () -> new TeaItem(ModBlocks.OOLONG_TEA.get(), tea().food(FoodList.OOLONG_TEA)));
    public static final Supplier<Item> DARK_TEA = ITEMS.register("dark_tea",
            () -> new TeaItem(ModBlocks.DARK_TEA.get(), tea().food(FoodList.DARK_TEA)));
    public static final Supplier<Item> ROSE_TEA = ITEMS.register("rose_tea",
            () -> new TeaItem(ModBlocks.ROSE_TEA.get(), tea().food(FoodList.ROSE_TEA)));
    public static final Supplier<Item> DANDELION_TEA = ITEMS.register("dandelion_tea",
            () -> new TeaItem(ModBlocks.DANDELION_TEA.get(), tea().food(FoodList.DANDELION_TEA)));
    public static final Supplier<Item> LILAC_TEA = ITEMS.register("lilac_tea",
            () -> new TeaItem(ModBlocks.LILAC_TEA.get(), tea().food(FoodList.LILAC_TEA)));
    public static final Supplier<Item> BLUE_ORCHID_TEA = ITEMS.register("blue_orchid_tea",
            () -> new TeaItem(ModBlocks.BLUE_ORCHID_TEA.get(), tea().food(FoodList.BLUE_ORCHID_TEA)));
    //Tea Latte
    public static final Supplier<Item> BAMBOO_TEA_LATTE = ITEMS.register("bamboo_tea_latte",
            () -> new TeaItem(ModBlocks.BAMBOO_TEA_LATTE.get(), tea().food(FoodList.BAMBOO_TEA_LATTE), true));
    public static final Supplier<Item> WHITE_TEA_LATTE = ITEMS.register("white_tea_latte",
            () -> new TeaItem(ModBlocks.WHITE_TEA_LATTE.get(), tea().food(FoodList.WHITE_TEA_LATTE), true));
    public static final Supplier<Item> GREEN_TEA_LATTE = ITEMS.register("green_tea_latte",
            () -> new TeaItem(ModBlocks.GREEN_TEA_LATTE.get(), tea().food(FoodList.GREEN_TEA_LATTE), true));
    public static final Supplier<Item> BLACK_TEA_LATTE = ITEMS.register("black_tea_latte",
            () -> new TeaItem(ModBlocks.BLACK_TEA_LATTE.get(), tea().food(FoodList.BLACK_TEA_LATTE), true));
    public static final Supplier<Item> YELLOW_TEA_LATTE = ITEMS.register("yellow_tea_latte",
            () -> new TeaItem(ModBlocks.YELLOW_TEA_LATTE.get(), tea().food(FoodList.YELLOW_TEA_LATTE), true));
    public static final Supplier<Item> OOLONG_TEA_LATTE = ITEMS.register("oolong_tea_latte",
            () -> new TeaItem(ModBlocks.OOLONG_TEA_LATTE.get(), tea().food(FoodList.OOLONG_TEA_LATTE), true));
    public static final Supplier<Item> DARK_TEA_LATTE = ITEMS.register("dark_tea_latte",
            () -> new TeaItem(ModBlocks.DARK_TEA_LATTE.get(), tea().food(FoodList.DARK_TEA_LATTE), true));
    public static final Supplier<Item> ROSE_TEA_LATTE = ITEMS.register("rose_tea_latte",
            () -> new TeaItem(ModBlocks.ROSE_TEA_LATTE.get(), tea().food(FoodList.ROSE_TEA_LATTE), true));
    public static final Supplier<Item> DANDELION_TEA_LATTE = ITEMS.register("dandelion_tea_latte",
            () -> new TeaItem(ModBlocks.DANDELION_TEA_LATTE.get(), tea().food(FoodList.DANDELION_TEA_LATTE), true));
    public static final Supplier<Item> LILAC_TEA_LATTE = ITEMS.register("lilac_tea_latte",
            () -> new TeaItem(ModBlocks.LILAC_TEA_LATTE.get(), tea().food(FoodList.LILAC_TEA_LATTE), true));
    public static final Supplier<Item> BLUE_ORCHID_TEA_LATTE = ITEMS.register("blue_orchid_tea_latte",
            () -> new TeaItem(ModBlocks.BLUE_ORCHID_TEA_LATTE.get(), tea().food(FoodList.BLUE_ORCHID_TEA_LATTE), true));

    private static Supplier<Item> registerTeaInProcessing(String pName, List<String> completedKeys, List<String> nextKeys) {
        return ITEMS.register(pName,
                () -> new TeaTooltipItem(new Item.Properties(), completedKeys, nextKeys));
    }
}