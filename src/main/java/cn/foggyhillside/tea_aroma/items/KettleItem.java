package cn.foggyhillside.tea_aroma.items;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.CupBlock;
import cn.foggyhillside.tea_aroma.blocks.KettleBlock;
import cn.foggyhillside.tea_aroma.blocks.TeaBlock;
import cn.foggyhillside.tea_aroma.blocks.entities.CupEntity;
import cn.foggyhillside.tea_aroma.blocks.entities.states.KettleLiquid;
import cn.foggyhillside.tea_aroma.component.KettleContents;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
import cn.foggyhillside.tea_aroma.registry.ModDataComponents;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import cn.foggyhillside.tea_aroma.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class KettleItem extends BlockItem {
    private static final int MAX_PROGRESS = 200;
    private static final int BOIL = 160;

    public KettleItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.MAIN_HAND && pPlayer.isShiftKeyDown()) {
            ItemStack stack = pPlayer.getItemInHand(pUsedHand);
            if (!getStackLiquid(stack).equals(KettleLiquid.NONE.toString()) || getStackAmount(stack) > 0) {
                if (!pLevel.isClientSide()) {
                    stack.set(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY);
                }
                pPlayer.playSound(ModSounds.KETTLE_POUR.get(), 1.0F, 1.0F);
                return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();
        if (getStackLiquid(stack).equals(KettleLiquid.NONE.toString()) && player != null) {
            BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (level.getFluidState(blockpos).is(Tags.Fluids.WATER)) {
                ItemStack filledStack = stack.copy();
                setStackLiquid(filledStack, KettleLiquid.WATER.toString());
                setStackAmount(filledStack, 3);
                fillKettle(stack, player, filledStack, pContext.getHand());
                player.playSound(ModSounds.KETTLE_FILL.get(), 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else if (level.getFluidState(blockpos).is(Tags.Fluids.MILK)) {
                ItemStack filledStack = stack.copy();
                setStackLiquid(filledStack, KettleLiquid.MILK.toString());
                setStackAmount(filledStack, 3);
                fillKettle(stack, player, filledStack, pContext.getHand());
                player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else if (level.getBlockState(blockpos).is(Blocks.WATER_CAULDRON)) {
                if (level.getBlockState(blockpos).getValue(LayeredCauldronBlock.LEVEL) > 1) {
                    level.setBlockAndUpdate(blockpos, level.getBlockState(blockpos).setValue(LayeredCauldronBlock.LEVEL, level.getBlockState(blockpos).getValue(LayeredCauldronBlock.LEVEL) - 1));
                } else {
                    level.setBlockAndUpdate(blockpos, Blocks.CAULDRON.defaultBlockState());
                }
                ItemStack filledStack = stack.copy();
                setStackLiquid(filledStack, KettleLiquid.WATER.toString());
                setStackAmount(filledStack, 3);
                fillKettle(stack, player, filledStack, pContext.getHand());
                player.playSound(ModSounds.KETTLE_FILL.get(), 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }

        } else if (player != null) {
            BlockPos blockPos = pContext.getClickedPos();
            InteractionHand hand = pContext.getHand();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            BlockState state = level.getBlockState(blockPos);

            if (blockEntity instanceof CupEntity cupEntity) {
                if (hand.equals(InteractionHand.MAIN_HAND)) {
                    if (!cupEntity.isEmpty() && !player.isShiftKeyDown()) {
                        SimpleContainer container = cupEntity.getInventoryContainer();
                        container.setItem(container.getContainerSize() - 1, stack);
                        Optional<RecipeHolder<BrewingRecipe>> recipe = level.getRecipeManager().getRecipeFor(BrewingRecipe.Type.INSTANCE, new RecipeInput() {
                            @Override
                            public ItemStack getItem(int pIndex) {
                                return container.getItem(pIndex);
                            }

                            @Override
                            public int size() {
                                return container.getContainerSize();
                            }
                        }, level);

                        if (recipe.isPresent()) {
                            if (recipe.get().value().getResultItem(level.registryAccess()).getItem() instanceof BlockItem result) {
                                if (!player.isCreative()) {
                                    consumeKettleAmount(stack);
                                }
                                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.KETTLE_POUR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                cupEntity.emptyInventory();
                                level.setBlockAndUpdate(blockPos, result.getBlock().defaultBlockState().setValue(CupBlock.FACING, state.getValue(CupBlock.FACING)));
                                return InteractionResult.SUCCESS;
                            }
                        }
                        if (!level.isClientSide()) {
                            player.displayClientMessage(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "client_message.cannot_brew")), true);
                        }
                        return InteractionResult.CONSUME;
                    }
                }
            } else if (state.getBlock() instanceof TeaBlock) {
                SimpleContainer container = new SimpleContainer(2);
                ItemStack teaStack = new ItemStack(state.getBlock());
                container.setItem(0, teaStack);
                container.setItem(1, stack.copy());
                Optional<RecipeHolder<FoamRecipe>> recipe = level.getRecipeManager().getRecipeFor(FoamRecipe.Type.INSTANCE, new RecipeInput() {
                    @Override
                    public ItemStack getItem(int pIndex) {
                        return container.getItem(pIndex);
                    }

                    @Override
                    public int size() {
                        return container.getContainerSize();
                    }
                }, level);

                if (recipe.isPresent()) {
                    if (recipe.get().value().getResultItem(level.registryAccess()).getItem() instanceof BlockItem result) {
                        if (!player.isCreative()) {
                            consumeKettleAmount(stack);
                        }
                        level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.KETTLE_POUR.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        level.setBlockAndUpdate(blockPos, result.getBlock().defaultBlockState().setValue(TeaBlock.FACING, state.getValue(TeaBlock.FACING)).setValue(TeaBlock.WITH_HONEY, state.getValue(TeaBlock.WITH_HONEY)).setValue(TeaBlock.WITH_SUGAR, state.getValue(TeaBlock.WITH_SUGAR)));
                        return InteractionResult.SUCCESS;
                    }
                }
                return InteractionResult.CONSUME;
            }
        }
        return super.useOn(pContext);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof Cow) {
            ItemStack filledStack = pStack.copy();
            setStackLiquid(filledStack, KettleLiquid.MILK.toString());
            setStackAmount(filledStack, 3);
            fillKettle(pStack, pPlayer, filledStack, pUsedHand);
            pPlayer.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);

            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        KettleContents contents = pStack.getOrDefault(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY);
        if (contents.liquid().equals(KettleLiquid.NONE.toString())) {
            pTooltipComponents.add(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "tooltip.kettle.none")).withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(Component.translatable("%s.%s".formatted("%s.%s".formatted(TeaAroma.MODID, "tooltip.kettle"), contents.liquid())).withStyle(ChatFormatting.GRAY));
            pTooltipComponents.add(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "tooltip.kettle.amount"), contents.amount()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext pContext) {
        BlockState state = super.getPlacementState(pContext);
        if (state == null) return null;
        KettleContents contents = pContext.getItemInHand().getOrDefault(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY);
        return state.setValue(KettleBlock.LIQUID, liquidFromString(contents.liquid())).setValue(KettleBlock.AMOUNT, contents.amount());
    }

    private static KettleLiquid liquidFromString(String name) {
        for (KettleLiquid liquid : KettleLiquid.values()) {
            if (liquid.getSerializedName().equals(name)) return liquid;
        }
        return KettleLiquid.NONE;
    }

    public static void fillKettle(ItemStack pEmptyStack, Player pPlayer, ItemStack pFilledStack, InteractionHand pUsedHand) {
        boolean flag = pPlayer.getAbilities().instabuild;
        pPlayer.awardStat(Stats.ITEM_USED.get(pEmptyStack.getItem()));
        if (flag) {
            if (!pPlayer.getInventory().contains(pFilledStack)) {
                pPlayer.getInventory().add(pFilledStack);
            }
        } else {
            pPlayer.setItemInHand(pUsedHand, pFilledStack);
        }
    }

    public static ItemStack getEmptyKettle() {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
        kettleStack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(KettleLiquid.NONE.toString(), 0, 0));
        return kettleStack;
    }

    public static ItemStack getBoilingWaterKettle() {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
        kettleStack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(KettleLiquid.BOILING_WATER.toString(), 3, 200));
        return kettleStack;
    }

    public static ItemStack getBoilingMilkKettle() {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
        kettleStack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(KettleLiquid.BOILING_MILK.toString(), 3, 200));
        return kettleStack;
    }

    public static String getStackLiquid(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY).liquid();
    }

    public static void setStackLiquid(ItemStack stack, String liquid) {
        KettleContents current = stack.getOrDefault(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY);
        stack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(liquid, current.amount(), current.boil_progress()));
    }

    public static int getStackAmount(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY).amount();
    }

    public static void setStackAmount(ItemStack stack, int amount) {
        KettleContents current = stack.getOrDefault(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY);
        stack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(current.liquid(), amount, current.boil_progress()));
    }

    public static void consumeKettleAmount(ItemStack stack) {
        KettleContents current = stack.getOrDefault(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY);
        int newAmount = current.amount() - 1;
        if (newAmount <= 0) {
            stack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(KettleLiquid.NONE.toString(), 0, 0));
        } else {
            stack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(current.liquid(), newAmount, current.boil_progress()));
        }
    }

}
