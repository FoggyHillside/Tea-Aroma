package cn.foggyhillside.tea_aroma.client.renderer;

import cn.foggyhillside.tea_aroma.blocks.BambooTrayBlock;
import cn.foggyhillside.tea_aroma.blocks.entities.CupEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CupRenderer implements BlockEntityRenderer<CupEntity> {
    public CupRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(CupEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Direction direction = (pBlockEntity.getBlockState().getValue(BambooTrayBlock.FACING)).getOpposite();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        for (int i = 0; i < 2; i++) {
            if (!pBlockEntity.getItemStack(i).isEmpty()) {
                pPoseStack.pushPose();
                pPoseStack.translate(0.5F, 0.1875F, 0.5F);
                pPoseStack.scale(0.25F, 0.25F, 0.25F);
                float f = -direction.toYRot();
                pPoseStack.mulPose(Axis.YP.rotationDegrees(f + 45F * i));
                ItemStack stack = pBlockEntity.getItemStack(i);
                BakedModel bakedModel = itemRenderer.getModel(stack, pBlockEntity.getLevel(), null, 0);
                itemRenderer.render(stack, ItemDisplayContext.FIXED, true, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, bakedModel);
                pPoseStack.popPose();
            }
        }
    }
}
