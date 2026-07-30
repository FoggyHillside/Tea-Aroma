package cn.foggyhillside.tea_aroma.blocks;

import cn.foggyhillside.tea_aroma.ModCompat;
import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.entities.CupEntity;
import cn.foggyhillside.tea_aroma.recipe.BrewingRecipe;
import cn.foggyhillside.tea_aroma.registry.ModItems;
import cn.foggyhillside.tea_aroma.registry.ModSounds;
import cn.foggyhillside.tea_aroma.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CupBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final VoxelShape SHAPE = Block.box(5.0F, 0.0F, 5.0F, 11.0F, 6.0F, 11.0F);

    public CupBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        ItemStack heldStack = pPlayer.getItemInHand(pHand);
        if (blockEntity instanceof CupEntity cupEntity) {
            if (pHand.equals(InteractionHand.MAIN_HAND)) {
                if (!heldStack.isEmpty() && !cupEntity.isFull()
                        && (heldStack.is(ModTags.TEA_INGREDIENTS) || (ModCompat.isSimplyTeaLoaded() && heldStack.is(ModTags.SIMPLYTEA_INGREDIENTS)))) {
                    if (cupEntity.addItem(heldStack, pPlayer)) {
                        return InteractionResult.SUCCESS;
                    }
                } else if (heldStack.isEmpty() && !cupEntity.isEmpty()) {
                    if (cupEntity.extractItem(pPlayer, pHand)) {
                        return InteractionResult.SUCCESS;
                    }
                } else if (heldStack.isEmpty() && cupEntity.isEmpty()) {
                    if (!pLevel.isClientSide()) {
                        pPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.CUP.get()));
                        pLevel.removeBlock(pPos, false);
                    }
                    return InteractionResult.SUCCESS;
                } else if ((heldStack.is(ModCompat.getIceCube()) || heldStack.is(ModCompat.getTeapotHot()) || heldStack.is(ModCompat.getTeapotFrothed()))
                        && !cupEntity.isEmpty()) {
                    SimpleContainer container = cupEntity.getInventoryContainer();
                    container.setItem(container.getContainerSize() - 1, heldStack);
                    Optional<BrewingRecipe> recipe = pLevel.getRecipeManager().getRecipeFor(BrewingRecipe.Type.INSTANCE, container, pLevel);

                    if (recipe.isPresent()) {
                        ItemStack resultStack = recipe.get().getResultItem(pLevel.registryAccess());
                        Item result = recipe.get().getResultItem(pLevel.registryAccess()).getItem();
                        if (result instanceof BlockItem) {
                            if (!pPlayer.isCreative()) {
                                ModCompat.teapotPour(heldStack, pPlayer, pHand);
                            }
                            pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.TEA_BREW.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                            cupEntity.emptyInventory();
                            pLevel.setBlockAndUpdate(pPos, ((BlockItem) result).getBlock().defaultBlockState().setValue(FACING, pState.getValue(FACING)));

                            return InteractionResult.SUCCESS;
                        } else if (recipe.get().getResultItem(pLevel.registryAccess()).is(ModTags.SIMPLYTEA_TEA)) {
                            ResourceLocation resultID = ForgeRegistries.ITEMS.getKey(resultStack.getItem());
                            Block resultBlock = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(TeaAroma.MODID + ":" + resultID.getPath()));
                            if (resultBlock != null) {
                                if (!pPlayer.isCreative()) {
                                    if (heldStack.is(ModCompat.getIceCube())) {
                                        heldStack.shrink(1);
                                    } else {
                                        ModCompat.teapotPour(heldStack, pPlayer, pHand);
                                    }
                                }
                                pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.TEA_BREW.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                                cupEntity.emptyInventory();
                                pLevel.setBlockAndUpdate(pPos, resultBlock.defaultBlockState().setValue(FACING, pState.getValue(FACING)));
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                    if (!pLevel.isClientSide()) {
                        pPlayer.displayClientMessage(Component.translatable("%s.%s".formatted(TeaAroma.MODID, "client_message.cannot_brew")), true);
                    }
                    return InteractionResult.CONSUME;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CupEntity cup) {
                Containers.dropContents(pLevel, pPos, cup.getInventoryList());
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState pState) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pDirection == Direction.DOWN && !pState.canSurvive(pLevel, pPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos floorPos = pPos.below();
        return canSupportRigidBlock(pLevel, floorPos) || canSupportCenter(pLevel, floorPos, Direction.UP);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof CupEntity cupEntity) {
            if (cupEntity.isFull()) {
                return 15;
            } else if (cupEntity.isEmpty()) {
                return 0;
            } else {
                return 8;
            }
        } else {
            return 0;
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CupEntity(pPos, pState);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

}
