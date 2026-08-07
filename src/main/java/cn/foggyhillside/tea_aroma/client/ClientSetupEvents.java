package cn.foggyhillside.tea_aroma.client;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.client.particle.SteamParticle;
import cn.foggyhillside.tea_aroma.client.renderer.BambooTrayRenderer;
import cn.foggyhillside.tea_aroma.client.renderer.CupRenderer;
import cn.foggyhillside.tea_aroma.registry.ModBlockEntities;
import cn.foggyhillside.tea_aroma.registry.ModParticleTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = TeaAroma.MODID, value = Dist.CLIENT)
public class ClientSetupEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CUP.get(), CupRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BAMBOO_TRAY.get(), BambooTrayRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.STEAM.get(), SteamParticle.Factory::new);
    }
}