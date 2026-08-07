package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.entities.BambooTrayEntity;
import cn.foggyhillside.tea_aroma.blocks.entities.CupEntity;
import cn.foggyhillside.tea_aroma.blocks.entities.KettleEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TeaAroma.MODID);

    public static final Supplier<BlockEntityType<KettleEntity>> KETTLE = BLOCK_ENTITIES.register("kettle",
            ()-> BlockEntityType.Builder.of(KettleEntity::new, ModBlocks.KETTLE.get()).build(null));
    public static final Supplier<BlockEntityType<CupEntity>> CUP = BLOCK_ENTITIES.register("cup",
            ()-> BlockEntityType.Builder.of(CupEntity::new, ModBlocks.CUP.get()).build(null));
    public static final Supplier<BlockEntityType<BambooTrayEntity>> BAMBOO_TRAY = BLOCK_ENTITIES.register("bamboo_tray",
            ()-> BlockEntityType.Builder.of(BambooTrayEntity::new, ModBlocks.BAMBOO_TRAY.get()).build(null));
}
