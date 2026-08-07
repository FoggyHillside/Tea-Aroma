package cn.foggyhillside.tea_aroma.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class SteamParticle extends TextureSheetParticle {
    protected SteamParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.scale(2.0F);
        this.setSize(0.25F, 0.25F);
        this.lifetime = this.random.nextInt(4) + 8;
        this.xd = pXSpeed;
        this.yd = pYSpeed + (double) (this.random.nextFloat() / 500.0F);
        this.zd = pZSpeed;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
            this.yd += this.random.nextFloat() / 500.0F;
            this.move(0, this.yd, 0);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
                this.alpha -= 0.02F;
            }
        } else {
            this.remove();
        }
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet sprite) {
            this.spriteSet = sprite;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            SteamParticle particle = new SteamParticle(pLevel, pX, pY + 0.3, pZ, pXSpeed, pYSpeed, pZSpeed);
            particle.setAlpha(0.6F);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
