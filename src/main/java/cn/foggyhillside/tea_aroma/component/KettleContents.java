package cn.foggyhillside.tea_aroma.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record KettleContents(String liquid, int amount, int boil_progress) {
    public KettleContents {
        amount = Math.max(0, amount);
    }

    public static final KettleContents EMPTY = new KettleContents("none", 0, 0);

    public static final Codec<KettleContents> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("liquid").forGetter(KettleContents::liquid),
                    Codec.INT.fieldOf("amount").forGetter(KettleContents::amount),
                    Codec.INT.fieldOf("boil_progress").forGetter(KettleContents::boil_progress)
            ).apply(instance, KettleContents::new));

    public static final StreamCodec<ByteBuf, KettleContents> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, KettleContents::liquid,
                    ByteBufCodecs.VAR_INT, KettleContents::amount,
                    ByteBufCodecs.VAR_INT, KettleContents::boil_progress,
                    KettleContents::new);
}
