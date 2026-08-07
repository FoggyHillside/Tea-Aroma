package cn.foggyhillside.tea_aroma.client.renderer;

import cn.foggyhillside.tea_aroma.blocks.BambooTrayBlock;
import cn.foggyhillside.tea_aroma.blocks.entities.BambooTrayEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BambooTrayRenderer implements BlockEntityRenderer<BambooTrayEntity> {
    public BambooTrayRenderer(BlockEntityRendererProvider.Context pContext) {}

    @Override
    public void render(BambooTrayEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (!pBlockEntity.getBlockState().getValue(BambooTrayBlock.PROCESS_TYPE).equals(3)) {
            if (pBlockEntity.getBlockState().getValue(BambooTrayBlock.PROCESS_TYPE).equals(2) && pBlockEntity.getProgress() == 1) {
                if (!pBlockEntity.getInventory().getStackInSlot(0).isEmpty()) {
                    int count = pBlockEntity.getInventory().getStackInSlot(0).getCount();
                    if (count > 12) {
                        renderItem(0, 0.375F, 0.1875F, 0.375F, 67.5F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                    }
                    if (count > 4) {
                        renderItem(0, 0.625F, 0.1825F, 0.625F, 112.5F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                    }
                    renderItem(0, 0.6875F, 0.19F, 0.375F, 45F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                }
                if (!pBlockEntity.getInventory().getStackInSlot(1).isEmpty()) {
                    renderItem(1, 0.375F, 0.195F, 0.625F, 135F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                }
            } else if (pBlockEntity.getBlockState().getValue(BambooTrayBlock.PROCESS_TYPE).equals(2) && pBlockEntity.getProgress() == 2) {
                if (!pBlockEntity.getInventory().getStackInSlot(0).isEmpty()) {
                    int count = pBlockEntity.getInventory().getStackInSlot(0).getCount();
                    if (count > 12) {
                        renderItem(0, 0.375F, 0.195F, 0.625F, 67.5F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                    }
                    if (count > 4) {
                        renderItem(0, 0.6875F, 0.1825F, 0.6875F, 135F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                    }
                    renderItem(0, 0.625F, 0.19F, 0.375F, 112.5F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                }
                if (!pBlockEntity.getInventory().getStackInSlot(1).isEmpty()) {
                    renderItem(1, 0.3125F, 0.1875F, 0.3125F, 45F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                }
            } else {
                if (!pBlockEntity.getInventory().getStackInSlot(0).isEmpty()) {
                    int count = pBlockEntity.getInventory().getStackInSlot(0).getCount();
                    if (count > 12) {
                        renderItem(0, 0.3125F, 0.195F, 0.6875F, 135F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                    }
                    if (count > 4) {
                        renderItem(0, 0.625F, 0.1825F, 0.625F, 67.5F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                    }
                    renderItem(0, 0.375F, 0.1875F, 0.3125F, 45F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                }
                if (!pBlockEntity.getInventory().getStackInSlot(1).isEmpty()) {
                    renderItem(1, 0.6875F, 0.19F, 0.3125F, 112.5F, pBlockEntity, pPoseStack, pBufferSource, pPackedLight, pPackedOverlay);
                }
            }
        }
    }

    private void renderItem(int slot, float x, float y, float z, float yRotation, BambooTrayEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = pBlockEntity.getInventory().getStackInSlot(slot);
        pPoseStack.pushPose();
        pPoseStack.translate(x, y, z);
        pPoseStack.scale(0.25F, 0.25F, 0.25F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(yRotation));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90F));
        BakedModel bakedModel = itemRenderer.getModel(stack, pBlockEntity.getLevel(), null, 0);
        itemRenderer.render(stack, ItemDisplayContext.FIXED, true, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, bakedModel);
        pPoseStack.popPose();
    }
}
