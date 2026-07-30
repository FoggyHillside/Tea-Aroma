package cn.foggyhillside.tea_aroma.blocks;

import cn.foggyhillside.tea_aroma.ModCompat;
import cn.foggyhillside.tea_aroma.recipe.FoamRecipe;
import cn.foggyhillside.tea_aroma.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class TATeaBlock extends TABlock {
    public TATeaBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand.equals(InteractionHand.MAIN_HAND)) {
            ItemStack heldStack = pPlayer.getItemInHand(pHand);
            if (!heldStack.isEmpty()) {
                if (ModCompat.isSimplyTeaLoaded() && heldStack.is(ModCompat.getTeapotFrothed())) {
                    SimpleContainer container = new SimpleContainer(2);
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(ForgeRegistries.BLOCKS.getKey(pLevel.getBlockState(pPos).getBlock()).toString()));
                    ItemStack stack = new ItemStack(item);
                    container.setItem(0, stack);
                    container.setItem(1, heldStack.copy());
                    Optional<FoamRecipe> recipe = pLevel.getRecipeManager().getRecipeFor(FoamRecipe.Type.INSTANCE, container, pLevel);

                    if (recipe.isPresent()) {
                        if (recipe.get().getResultItem(pLevel.registryAccess()).getItem() instanceof BlockItem result) {
                            if (!pPlayer.isCreative()) {
                                ModCompat.teapotPour(heldStack, pPlayer, pHand);
                            }
                            pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.TEA_BREW.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                            pLevel.setBlockAndUpdate(pPos, result.getBlock().defaultBlockState().setValue(FACING, pState.getValue(FACING)).setValue(WITH_HONEY, pState.getValue(WITH_HONEY)).setValue(WITH_SUGAR, pState.getValue(WITH_SUGAR)));
                            return InteractionResult.SUCCESS;
                        }
                    }
                    return InteractionResult.CONSUME;
                } else {
                    if (pLevel.isClientSide()
                            && ((heldStack.is(Items.HONEY_BOTTLE) && !pState.getValue(WITH_HONEY))
                            || (heldStack.is(Items.SUGAR) && !pState.getValue(WITH_SUGAR)))) {
                        return InteractionResult.CONSUME;
                    }
                    if (flavour(pLevel, pState, pPos, pPlayer, pHand, WITH_SUGAR, Items.SUGAR)) {
                        return InteractionResult.SUCCESS;
                    } else if (flavour(pLevel, pState, pPos, pPlayer, pHand, WITH_HONEY, Items.HONEY_BOTTLE)) {
                        return InteractionResult.SUCCESS;
                    }
                }
            } else {
                pick(pLevel, pState, pPos, pPlayer, pHand);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

}
