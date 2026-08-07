package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.ModCompat;
import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.items.KettleItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TeaAroma.MODID);

    public static final Supplier<CreativeModeTab> TEA_AROMA_TAB = CREATIVE_MODE_TABS.register(TeaAroma.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + TeaAroma.MODID))
            .icon(() -> ModItems.GREEN_TEA.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(KettleItem.getEmptyKettle());
                output.accept(KettleItem.getBoilingWaterKettle());
                output.accept(KettleItem.getBoilingMilkKettle());
                output.accept(ModItems.CUP.get());
                output.accept(ModItems.BAMBOO_TRAY.get());
                output.accept(ModItems.TEA_SAPLING.get());
                if (!ModCompat.isFestivalDelicaciesLoaded()) {
                    output.accept(ModItems.BAMBOO_LEAVES.get());
                }
                output.accept(ModItems.FRESH_TEA_LEAVES.get());
                output.accept(ModItems.BAMBOO_TEA_LEAVES.get());
                output.accept(ModItems.WHITE_TEA_LEAVES.get());
                output.accept(ModItems.GREEN_TEA_LEAVES.get());
                output.accept(ModItems.BLACK_TEA_LEAVES.get());
                output.accept(ModItems.YELLOW_TEA_LEAVES.get());
                output.accept(ModItems.OOLONG_TEA_LEAVES.get());
                output.accept(ModItems.DARK_TEA_LEAVES.get());
                output.accept(ModItems.LILAC_TEA_LEAVES.get());
                output.accept(ModItems.ROSE_TEA_LEAVES.get());
                output.accept(ModItems.BLUE_ORCHID_TEA_LEAVES.get());
                output.accept(ModItems.DANDELION_TEA_LEAVES.get());
                output.accept(ModItems.BAMBOO_TEA.get());
                output.accept(ModItems.WHITE_TEA.get());
                output.accept(ModItems.GREEN_TEA.get());
                output.accept(ModItems.BLACK_TEA.get());
                output.accept(ModItems.YELLOW_TEA.get());
                output.accept(ModItems.OOLONG_TEA.get());
                output.accept(ModItems.DARK_TEA.get());
                output.accept(ModItems.LILAC_TEA.get());
                output.accept(ModItems.ROSE_TEA.get());
                output.accept(ModItems.BLUE_ORCHID_TEA.get());
                output.accept(ModItems.DANDELION_TEA.get());
                output.accept(ModItems.BAMBOO_TEA_LATTE.get());
                output.accept(ModItems.WHITE_TEA_LATTE.get());
                output.accept(ModItems.GREEN_TEA_LATTE.get());
                output.accept(ModItems.BLACK_TEA_LATTE.get());
                output.accept(ModItems.YELLOW_TEA_LATTE.get());
                output.accept(ModItems.OOLONG_TEA_LATTE.get());
                output.accept(ModItems.DARK_TEA_LATTE.get());
                output.accept(ModItems.LILAC_TEA_LATTE.get());
                output.accept(ModItems.ROSE_TEA_LATTE.get());
                output.accept(ModItems.BLUE_ORCHID_TEA_LATTE.get());
                output.accept(ModItems.DANDELION_TEA_LATTE.get());
            }).build());
}
