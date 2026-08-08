package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.TeaAroma;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, TeaAroma.MODID);

    public static final Supplier<SoundEvent> ITEM_TEA_LEAVES_PICK_FROM_TREE = SOUNDS.register("block.tea_tree.pick", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TeaAroma.MODID, "block.tea_tree.pick")));
    public static final Supplier<SoundEvent> KETTLE_FILL = SOUNDS.register("block.kettle.fill", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TeaAroma.MODID, "block.kettle.fill")));
    public static final Supplier<SoundEvent> KETTLE_POUR = SOUNDS.register("block.kettle_pour", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TeaAroma.MODID, "block.kettle_pour")));
    public static final Supplier<SoundEvent> TEA_PROCESSING_2 = SOUNDS.register("block.tea_processing_2", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TeaAroma.MODID, "block.tea_processing_2")));
}