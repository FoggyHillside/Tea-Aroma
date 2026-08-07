package cn.foggyhillside.tea_aroma.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record TeaContents(boolean withHoney, boolean withSugar) {
    public static final TeaContents EMPTY = new TeaContents(false, false);

    public static final Codec<TeaContents> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("with_honey", false).forGetter(TeaContents::withHoney),
                    Codec.BOOL.optionalFieldOf("with_sugar", false).forGetter(TeaContents::withSugar)
            ).apply(instance, TeaContents::new));

    public static final StreamCodec<ByteBuf, TeaContents> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, TeaContents::withHoney,
                    ByteBufCodecs.BOOL, TeaContents::withSugar,
                    TeaContents::new);
}