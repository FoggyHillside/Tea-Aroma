package cn.foggyhillside.tea_aroma.blocks;

import cn.foggyhillside.tea_aroma.component.TeaContents;
import cn.foggyhillside.tea_aroma.registry.ModBlockStateProperties;
import cn.foggyhillside.tea_aroma.registry.ModDataComponents;
import cn.foggyhillside.tea_aroma.registry.ModParticleTypes;
import cn.foggyhillside.tea_aroma.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TeaBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WITH_HONEY = ModBlockStateProperties.WITH_HONEY;
    public static final BooleanProperty WITH_SUGAR = ModBlockStateProperties.WITH_SUGAR;
    private static final VoxelShape SHAPE = Block.box(5.0F, 0.0F, 5.0F, 11.0F, 6.0F, 11.0F);

    public TeaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WITH_HONEY, false).setValue(WITH_SUGAR, false));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState pState) {
        return true;
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        pBuilder.add(WITH_HONEY);
        pBuilder.add(WITH_SUGAR);
    }

    @Override
    protected BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pDirection == Direction.DOWN && !pState.canSurvive(pLevel, pPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos floorPos = pPos.below();
        return canSupportRigidBlock(pLevel, floorPos) || canSupportCenter(pLevel, floorPos, Direction.UP);
    }

    @Override
    protected BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.setValue(FACING, pMirror.mirror(pState.getValue(FACING)));
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        RandomSource random = pLevel.random;
        Block block = pLevel.getBlockState(pPos).getBlock();
        if (block instanceof TeaBlock) {
            double x = pPos.getX() + 0.5;
            double y = pPos.getY();
            double z = pPos.getZ() + 0.5;

            double offset = random.nextDouble() * 0.2 - 0.1;
            double yOffset = random.nextDouble() * 4.0 / 16.0;
            pLevel.addParticle(ModParticleTypes.STEAM.get(), x + offset, y + 0.32 + yOffset, z + offset, 0, 0, 0);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (!pPlayer.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }

        pick(pLevel, pState, pPos, pPlayer);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pHand != InteractionHand.MAIN_HAND) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (pLevel.isClientSide()) return ItemInteractionResult.CONSUME;
        if (pStack.is(Items.SUGAR) && !pState.getValue(WITH_SUGAR)) {
            flavour(pLevel, pState, pPos, pPlayer, pHand, WITH_SUGAR);
            return ItemInteractionResult.SUCCESS;
        }
        if (pStack.is(Items.HONEY_BOTTLE) && !pState.getValue(WITH_HONEY)) {
            flavour(pLevel, pState, pPos, pPlayer, pHand, WITH_HONEY);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void flavour(Level level, BlockState state, BlockPos pos, Player player, InteractionHand hand, BooleanProperty property) {
        ItemStack held = player.getItemInHand(hand);
        Utils.addItem(held, player, held.getCraftingRemainingItem());
        level.setBlockAndUpdate(pos, state.setValue(property, true));
    }

    private void pick(Level level, BlockState state, BlockPos pos, Player player) {
        if (!level.isClientSide()) {
            ItemStack stack = new ItemStack(state.getBlock());
            stack.set(ModDataComponents.TEA_CONTENTS.get(), new TeaContents(state.getValue(WITH_HONEY), state.getValue(WITH_SUGAR)));
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            level.removeBlock(pos, false);
        }
    }
}
