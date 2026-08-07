package cn.foggyhillside.tea_aroma.registry;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.component.KettleContents;
import cn.foggyhillside.tea_aroma.component.TeaContents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, TeaAroma.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KettleContents>> KETTLE_CONTENTS = DATA_COMPONENTS.registerComponentType("kettle_contents", builder ->
            builder.persistent(KettleContents.CODEC).networkSynchronized(KettleContents.STREAM_CODEC).cacheEncoding());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TeaContents>> TEA_CONTENTS = DATA_COMPONENTS.registerComponentType("tea_contents", builder ->
            builder.persistent(TeaContents.CODEC).networkSynchronized(TeaContents.STREAM_CODEC).cacheEncoding());
}
