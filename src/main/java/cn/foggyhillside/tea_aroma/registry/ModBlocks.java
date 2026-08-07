package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, TeaAroma.MODID);

    public static final Supplier<Block> TEA_TREE = BLOCKS.register("tea_tree",
            () -> new TeaTreeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH)));

    public static final Supplier<Block> KETTLE = BLOCKS.register("kettle",
            () -> new KettleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(0.5F, 6.0F).sound(SoundType.LANTERN).pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> CUP = BLOCKS.register("cup",
            () -> new CupBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> BAMBOO_TRAY = BLOCKS.register("bamboo_tray",
            () -> new BambooTrayBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).instrument(NoteBlockInstrument.BASS).noOcclusion().strength(0.5F).pushReaction(PushReaction.DESTROY)));
    
    //Tea
    public static final Supplier<Block> BAMBOO_TEA = BLOCKS.register("bamboo_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> WHITE_TEA = BLOCKS.register("white_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> GREEN_TEA = BLOCKS.register("green_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> BLACK_TEA = BLOCKS.register("black_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> YELLOW_TEA = BLOCKS.register("yellow_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> OOLONG_TEA = BLOCKS.register("oolong_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> DARK_TEA = BLOCKS.register("dark_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> ROSE_TEA = BLOCKS.register("rose_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> DANDELION_TEA = BLOCKS.register("dandelion_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> LILAC_TEA = BLOCKS.register("lilac_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> BLUE_ORCHID_TEA = BLOCKS.register("blue_orchid_tea",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    //Tea Latte
    public static final Supplier<Block> BAMBOO_TEA_LATTE = BLOCKS.register("bamboo_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> WHITE_TEA_LATTE = BLOCKS.register("white_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> GREEN_TEA_LATTE = BLOCKS.register("green_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> BLACK_TEA_LATTE = BLOCKS.register("black_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> YELLOW_TEA_LATTE = BLOCKS.register("yellow_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> OOLONG_TEA_LATTE = BLOCKS.register("oolong_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> DARK_TEA_LATTE = BLOCKS.register("dark_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> ROSE_TEA_LATTE = BLOCKS.register("rose_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> DANDELION_TEA_LATTE = BLOCKS.register("dandelion_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> LILAC_TEA_LATTE = BLOCKS.register("lilac_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> BLUE_ORCHID_TEA_LATTE = BLOCKS.register("blue_orchid_tea_latte",
            () -> new TeaBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));

}
