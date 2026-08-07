package cn.foggyhillside.tea_aroma.items;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.TeaBlock;
import cn.foggyhillside.tea_aroma.component.TeaContents;
import cn.foggyhillside.tea_aroma.registry.ModDataComponents;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.EffectCures;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeaItem extends BlockItem {
    private final boolean isLatte;

    public TeaItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        this.isLatte = false;
    }

    public TeaItem(Block pBlock, Properties pProperties, boolean pIsLatte) {
        super(pBlock, pProperties);
        this.isLatte = pIsLatte;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext pContext) {
        BlockState state = super.getPlacementState(pContext);
        if (state == null) return null;
        TeaContents contents = pContext.getItemInHand()
                .getOrDefault(ModDataComponents.TEA_CONTENTS.get(), TeaContents.EMPTY);
        return state
                .setValue(TeaBlock.WITH_HONEY, contents.withHoney())
                .setValue(TeaBlock.WITH_SUGAR, contents.withSugar());
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        if (pContext.getPlayer() != null && pContext.getPlayer().isShiftKeyDown()) {
            return super.canPlace(pContext, pState);
        }
        return false;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        super.finishUsingItem(pStack, pLevel, pEntityLiving);
        TeaContents contents = pStack.getOrDefault(ModDataComponents.TEA_CONTENTS.get(), TeaContents.EMPTY);
        if (isLatte && !pLevel.isClientSide) {
            List<MobEffectInstance> effects = new ArrayList<>(pEntityLiving.getActiveEffects());
            List<MobEffectInstance> milkCurable = effects.stream()
                    .filter(effect -> effect.getCures().contains(EffectCures.MILK))
                    .toList();
            if (!milkCurable.isEmpty()) {
                MobEffectInstance selected = milkCurable.get(pLevel.random.nextInt(milkCurable.size()));
                pEntityLiving.removeEffect(selected.getEffect());
            }
        }
        if (contents.withHoney() && !pLevel.isClientSide) {
            pEntityLiving.removeEffect(MobEffects.POISON);
        }
        if (contents.withSugar() && !pLevel.isClientSide) {
            pEntityLiving.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 25 * 20, 0));
        }
        if (pEntityLiving instanceof Player player) {
            return ItemUtils.createFilledResult(pStack, player, new ItemStack(ModItems.CUP.get()), false);
        } else {
            pStack.consume(1, pEntityLiving);
            return pStack;
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        TeaContents contents = pStack.getOrDefault(ModDataComponents.TEA_CONTENTS.get(), TeaContents.EMPTY);
        if (contents.withHoney()) {
            pTooltipComponents.add(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "tooltip.tea.honey")).withStyle(ChatFormatting.GRAY));
        }
        if (contents.withSugar()) {
            pTooltipComponents.add(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "tooltip.tea.sugar")).withStyle(ChatFormatting.GRAY));
        }

        FoodProperties foodStats = pStack.getFoodProperties(null);
        if (foodStats == null) {
            return;
        }

        List<MobEffectInstance> effects = foodStats.effects().stream().map(FoodProperties.PossibleEffect::effect).toList();
        if (!effects.isEmpty()) {
            PotionContents.addPotionTooltip(effects, pTooltipComponents::add, 1.0F, pContext.tickRate());
        }
    }
}