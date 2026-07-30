package cn.foggyhillside.tea_aroma.items;

import cn.foggyhillside.tea_aroma.ModCompat;
import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.CupBlock;
import cn.foggyhillside.tea_aroma.blocks.TATeaBlock;
import cn.foggyhillside.tea_aroma.blocks.entities.CupEntity;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import cn.foggyhillside.tea_aroma.registry.ModSounds;
import cn.foggyhillside.tea_aroma.registry.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class KettleItem extends BlockItem {
    private static final int MAX_PROGRESS = 200;
    private static final int BOIL = 160;

    public KettleItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        ItemStack stack = pContext.getItemInHand();
        correctTag(stack);
        if (getStackLiquid(stack).equals("none") && player != null) {
            BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (level.getFluidState(blockpos).is(Fluids.WATER)) {
                ItemStack filledStack = stack.copy();
                setStackLiquid(filledStack, "water");
                setStackAmount(filledStack, 3);
                fillKettle(stack, player, filledStack, pContext.getHand());
                player.playSound(ModSounds.KETTLE_FILL.get(), 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else if (level.getBlockState(blockpos).is(Blocks.WATER_CAULDRON)) {
                if (level.getBlockState(blockpos).getValue(LayeredCauldronBlock.LEVEL) > 1) {
                    level.setBlockAndUpdate(blockpos, level.getBlockState(blockpos).setValue(LayeredCauldronBlock.LEVEL, level.getBlockState(blockpos).getValue(LayeredCauldronBlock.LEVEL) - 1));
                } else {
                    level.setBlockAndUpdate(blockpos, Blocks.CAULDRON.defaultBlockState());
                }
                ItemStack filledStack = stack.copy();
                setStackLiquid(filledStack, "water");
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
                    if (!((CupEntity) blockEntity).isEmpty() && !player.isShiftKeyDown()) {
                        SimpleContainer container = ((CupEntity) blockEntity).getInventoryContainer();
                        container.setItem(container.getContainerSize() - 1, stack);
                        Optional<BrewingRecipe> recipe = level.getRecipeManager().getRecipeFor(BrewingRecipe.Type.INSTANCE, container, level);

                        if (recipe.isPresent()) {
                            if (recipe.get().getResultItem(level.registryAccess()).getItem() instanceof BlockItem result) {
                                if (!player.isCreative()) {
                                    setStackAmount(stack, getStackAmount(stack) - 1);
                                    correctTag(stack);
                                }
                                level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ModSounds.TEA_BREW.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                                cupEntity.emptyInventory();
                                level.setBlockAndUpdate(blockPos, result.getBlock().defaultBlockState().setValue(CupBlock.FACING, state.getValue(CupBlock.FACING)));
                                return InteractionResult.SUCCESS;
                            } else if (ModCompat.isSimplyTeaLoaded() && recipe.get().getResultItem(level.registryAccess()).is(ModTags.SIMPLYTEA_TEA)) {
                                ItemStack result = recipe.get().getResultItem(level.registryAccess());
                                ResourceLocation resultID = ForgeRegistries.ITEMS.getKey(result.getItem());
                                Block resultBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(TeaAroma.MODID + ":" + resultID.getPath()));
                                if (resultBlock != null) {
                                    if (!player.isCreative()) {
                                        setStackAmount(stack, getStackAmount(stack) - 1);
                                        correctTag(stack);
                                    }
                                    level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ModSounds.TEA_BREW.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                                    cupEntity.emptyInventory();
                                    level.setBlockAndUpdate(blockPos, resultBlock.defaultBlockState());
                                    return InteractionResult.SUCCESS;
                                }
                            }
                        }
                        if (!level.isClientSide()) {
                            player.displayClientMessage(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "client_message.cannot_brew")), true);
                        }
                        return InteractionResult.CONSUME;
                    }
                }
            } else if (state.getBlock() instanceof TATeaBlock) {
                SimpleContainer container = new SimpleContainer(2);
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString()));
                ItemStack teaStack = new ItemStack(item);
                container.setItem(0, teaStack);
                container.setItem(1, stack.copy());
                Optional<FoamRecipe> recipe = level.getRecipeManager().getRecipeFor(FoamRecipe.Type.INSTANCE, container, level);

                if (recipe.isPresent()) {
                    if (recipe.get().getResultItem(level.registryAccess()).getItem() instanceof BlockItem result) {
                        if (!player.isCreative()) {
                            setStackAmount(stack, getStackAmount(stack) - 1);
                            correctTag(stack);
                        }
                        level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), ModSounds.TEA_BREW.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                        level.setBlockAndUpdate(blockPos, result.getBlock().defaultBlockState().setValue(TATeaBlock.FACING, state.getValue(TATeaBlock.FACING)).setValue(TATeaBlock.WITH_HONEY, state.getValue(TATeaBlock.WITH_HONEY)).setValue(TATeaBlock.WITH_SUGAR, state.getValue(TATeaBlock.WITH_SUGAR)));
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
            correctTag(pStack);
            ItemStack filledStack = pStack.copy();
            setStackLiquid(filledStack, "milk");
            setStackAmount(filledStack, 3);
            fillKettle(pStack, pPlayer, filledStack, pUsedHand);
            pPlayer.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);

            return InteractionResult.SUCCESS;
        }

        return super.interactLivingEntity(pStack, pPlayer, pInteractionTarget, pUsedHand);
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (stack.hasTag()) {
            if (getStackLiquid(stack).equals("none")) {
                tooltip.add(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "tooltip.kettle.none")).withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("%s.%s".formatted("%s.%s".formatted(TeaAroma.MODID, "tooltip.kettle"), getStackLiquid(stack))).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "tooltip.kettle.amount"), getStackAmount(stack)).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "tooltip.kettle.none")).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void correctTag(ItemStack pStack) {
        correctLiquid(pStack);
        correctAmount(pStack);
        correctBoilProgress(pStack);
        if (!(getStackAmount(pStack) == 0 && getStackLiquid(pStack).equals("none") && getStackBoilProgress(pStack) == 0)) {
            if (getStackAmount(pStack) == 0
                    && !getStackLiquid(pStack).equals("none")) {
                setStackLiquid(pStack, "none");
            } else if (getStackAmount(pStack) != 0
                    && getStackLiquid(pStack).equals("none")) {
                setStackAmount(pStack, 0);
            } else if (!getStackLiquid(pStack).equals("none")) {
                if (isCool(pStack)) {
                    if (getStackBoilProgress(pStack) >= BOIL) {
                        boil(pStack);
                    }
                } else {
                    if (getStackBoilProgress(pStack) < BOIL) {
                        cool(pStack);
                    }
                }
            }
        }
    }

    private static void correctLiquid(ItemStack stack) {
        if (!(getStackLiquid(stack).equals("none")
                || getStackLiquid(stack).equals("water")
                || getStackLiquid(stack).equals("milk")
                || getStackLiquid(stack).equals("boiling_water")
                || getStackLiquid(stack).equals("boiling_milk"))) {
            setStackLiquid(stack, "none");
        }
    }

    private static void correctAmount(ItemStack stack) {
        if (getStackAmount(stack) > 3) {
            setStackAmount(stack, 3);
        } else if (getStackAmount(stack) < 0) {
            setStackAmount(stack, 0);
        } else if (getStackAmount(stack) == 0) {
            //if the stack has no amount tag, getStackAmount() will also return 0
            setStackAmount(stack, 0);
        }
    }

    private static void correctBoilProgress(ItemStack stack) {
        if (getStackLiquid(stack).equals("none")) {
            setStackBoilProgress(stack, 0);
        } else {
            if (getStackBoilProgress(stack) > MAX_PROGRESS) {
                setStackBoilProgress(stack, MAX_PROGRESS);
            } else if (getStackBoilProgress(stack) < 0) {
                setStackBoilProgress(stack, 0);
            } else if (getStackBoilProgress(stack) == 0) {
                //if the stack has no boil_progress tag, getStackBoilProgress() will also return 0
                setStackBoilProgress(stack, 0);
            }
        }
    }

    public static String getStackLiquid(ItemStack stack) {
        return stack.getOrCreateTagElement("BlockStateTag").getString("liquid");
    }

    public static void setStackLiquid(ItemStack stack, String liquid) {
        stack.getOrCreateTagElement("BlockStateTag").remove("liquid");
        stack.getOrCreateTagElement("BlockStateTag").putString("liquid", liquid);
    }

    public static int getStackAmount(ItemStack stack) {
        return stack.getOrCreateTagElement("BlockStateTag").getInt("amount");
    }

    public static void setStackAmount(ItemStack stack, int amount) {
        stack.getOrCreateTagElement("BlockStateTag").remove("amount");
        stack.getOrCreateTagElement("BlockStateTag").putInt("amount", amount);
    }

    private static int getStackBoilProgress(ItemStack stack) {
        return stack.getOrCreateTagElement("BlockEntityTag").getInt("boil_progress");
    }

    private static void setStackBoilProgress(ItemStack stack, int boilProgress) {
        stack.getOrCreateTagElement("BlockEntityTag").remove("boil_progress");
        stack.getOrCreateTagElement("BlockEntityTag").putInt("boil_progress", boilProgress);
    }

    private static void boil(ItemStack stack) {
        if (getStackLiquid(stack).equals("water")) {
            setStackLiquid(stack, "boiling_water");
        } else if (getStackLiquid(stack).equals("milk")) {
            setStackLiquid(stack, "boiling_milk");
        }
    }

    private static void cool(ItemStack stack) {
        if (getStackLiquid(stack).equals("boiling_water")) {
            setStackLiquid(stack, "water");
        } else if (getStackLiquid(stack).equals("boiling_milk")) {
            setStackLiquid(stack, "milk");
        }
    }

    private static boolean isCool(ItemStack stack) {
        return getStackLiquid(stack).equals("water") || getStackLiquid(stack).equals("milk");
    }

    public static ItemStack getEmptyKettle() {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
        kettleStack.getOrCreateTagElement("BlockEntityTag").putInt("boil_progress", 0);
        kettleStack.getOrCreateTagElement("BlockStateTag").putInt("amount", 0);
        kettleStack.getOrCreateTagElement("BlockStateTag").putString("liquid", "none");
        return kettleStack;
    }

    public static ItemStack getBoilingWaterKettle() {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
        kettleStack.getOrCreateTagElement("BlockEntityTag").putInt("boil_progress", 200);
        kettleStack.getOrCreateTagElement("BlockStateTag").putInt("amount", 3);
        kettleStack.getOrCreateTagElement("BlockStateTag").putString("liquid", "boiling_water");
        return kettleStack;
    }

    public static ItemStack getBoilingMilkKettle() {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
        kettleStack.getOrCreateTagElement("BlockEntityTag").putInt("boil_progress", 200);
        kettleStack.getOrCreateTagElement("BlockStateTag").putInt("amount", 3);
        kettleStack.getOrCreateTagElement("BlockStateTag").putString("liquid", "boiling_milk");
        return kettleStack;
    }
}
