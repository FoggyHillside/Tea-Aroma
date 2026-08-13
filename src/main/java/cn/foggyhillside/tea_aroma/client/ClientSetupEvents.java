package cn.foggyhillside.tea_aroma.client;

import cn.foggyhillside.tea_aroma.ModCompat;
import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.client.particle.SteamParticle;
import cn.foggyhillside.tea_aroma.client.renderer.BambooTrayRenderer;
import cn.foggyhillside.tea_aroma.client.renderer.CupRenderer;
import cn.foggyhillside.tea_aroma.registry.ModBlockEntities;
import cn.foggyhillside.tea_aroma.registry.ModParticleTypes;
import cn.foggyhillside.tea_aroma.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = TeaAroma.MODID, value = Dist.CLIENT)
public class ClientSetupEvents {
    private static final ResourceLocation COMPAT_BAMBOO = ResourceLocation.parse("festival_delicacies:bamboo_leaves");
    private static Item compatBambooLeaves;

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CUP.get(), CupRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BAMBOO_TRAY.get(), BambooTrayRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticleTypes.STEAM.get(), SteamParticle.Factory::new);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!ModCompat.isFestivalDelicaciesLoaded()) return;
        if (compatBambooLeaves == null)
            compatBambooLeaves = BuiltInRegistries.ITEM.get(COMPAT_BAMBOO);
        if (compatBambooLeaves != Items.AIR && event.getItemStack().is(compatBambooLeaves)) {
            event.getToolTip().add(Utils.buildProcessTooltip(List.of(), List.of(TeaAroma.MODID + ".tooltip.drying")));
        }
    }
}