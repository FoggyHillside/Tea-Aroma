package cn.foggyhillside.tea_aroma.blocks;

import cn.foggyhillside.tea_aroma.blocks.entities.KettleEntity;
import cn.foggyhillside.tea_aroma.blocks.entities.states.KettleLiquid;
import cn.foggyhillside.tea_aroma.blocks.entities.states.KettleSupport;
import cn.foggyhillside.tea_aroma.component.KettleContents;
import cn.foggyhillside.tea_aroma.registry.*;
import cn.foggyhillside.tea_aroma.util.Utils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KettleBlock extends BaseEntityBlock {
    public static final MapCodec<KettleBlock> CODEC = simpleCodec(KettleBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty AMOUNT = ModBlockStateProperties.AMOUNT;
    public static final EnumProperty<KettleSupport> SUPPORT = EnumProperty.create("support", KettleSupport.class);
    public static final EnumProperty<KettleLiquid> LIQUID = EnumProperty.create("liquid", KettleLiquid.class);
    protected static final VoxelShape SHAPE = Block.box(3.0F, 0.0F, 3.0F, 13.0F, 8.0F, 13.0F);
    protected static final VoxelShape SHAPE_WITH_TRAY = Shapes.or(SHAPE, Block.box(0.0F, -1.0F, 0.0F, 16.0F, 0.0F, 16.0F));

    public KettleBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(AMOUNT, 0).setValue(SUPPORT, KettleSupport.NONE).setValue(LIQUID, KettleLiquid.NONE));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = (defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                .setValue(AMOUNT, 0)
                .setValue(LIQUID, KettleLiquid.NONE));
        return pContext.getClickedFace().equals(Direction.DOWN) ? state.setValue(SUPPORT, KettleSupport.HANDLE) : state.setValue(SUPPORT, this.getTrayState(level, pos));
    }

    private KettleSupport getTrayState(LevelAccessor pLevel, BlockPos pPos) {
        return pLevel.getBlockState(pPos.below()).is(ModTags.TRAY_HEAT_SOURCES) ? KettleSupport.TRAY : KettleSupport.NONE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new KettleEntity(pPos, pState);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get(), 1);
        int boilProgress = 0;
        if (pParams.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof KettleEntity kettleEntity) {
            boilProgress = kettleEntity.getBoilProgress();
        }
        kettleStack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(
                pState.getValue(KettleBlock.LIQUID).toString(),
                pState.getValue(KettleBlock.AMOUNT),
                boilProgress
        ));
        return ObjectArrayList.of(kettleStack);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pPlayer.isShiftKeyDown()) {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(SUPPORT, pState.getValue(SUPPORT).equals(KettleSupport.HANDLE) ? this.getTrayState(pLevel, pPos) : KettleSupport.HANDLE));
            pLevel.playSound(null, pPos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
        } else if (!pLevel.isClientSide()) {
            ItemStack stack = getKettleStack(pState, pLevel, pPos);
            if (pPlayer.getMainHandItem().isEmpty()) {
                pPlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
            } else if (!pPlayer.getInventory().add(stack)) {
                return InteractionResult.PASS;
            }
            pLevel.removeBlock(pPos, false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (pState.getValue(LIQUID).equals(KettleLiquid.NONE)) {
            boolean isWaterPotion = pStack.getItem().equals(Items.POTION)
                    && pStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(Potions.WATER);
            if (pStack.is(ModTags.WATER) || isWaterPotion) {
                if (!pLevel.isClientSide()) {
                    ItemStack remainingStack;
                    if (pStack.getItem().equals(Items.POTION)) {
                        remainingStack = new ItemStack(Items.GLASS_BOTTLE);
                    } else {
                        remainingStack = pStack.getCraftingRemainingItem();
                    }
                    Utils.addItem(pStack, pPlayer, remainingStack);
                    pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.KETTLE_FILL.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(LIQUID, KettleLiquid.WATER).setValue(AMOUNT, 3));
                }
                return ItemInteractionResult.SUCCESS;
            } else if (pStack.is(ModTags.MILK)) {
                if (!pLevel.isClientSide()) {
                    Utils.addItem(pStack, pPlayer, pStack.getCraftingRemainingItem());
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(LIQUID, KettleLiquid.MILK).setValue(AMOUNT, 3));
                    pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), ModSounds.KETTLE_FILL.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemStack getKettleStack(BlockState pState, Level pLevel, BlockPos pPos) {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get(), 1);
        if (pLevel.getBlockEntity(pPos) instanceof KettleEntity kettleEntity) {
            kettleStack.set(ModDataComponents.KETTLE_CONTENTS.get(), new KettleContents(
                    pState.getValue(KettleBlock.LIQUID).toString(),
                    pState.getValue(KettleBlock.AMOUNT),
                    kettleEntity.getBoilProgress()
            ));
        }
        return kettleStack;
    }

    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return (pState.getValue(SUPPORT)).equals(KettleSupport.TRAY) ? SHAPE_WITH_TRAY : SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        return pDirection.getAxis().equals(Direction.Axis.Y) && !pState.getValue(SUPPORT).equals(KettleSupport.HANDLE) ? pState.setValue(SUPPORT, this.getTrayState(pLevel, pPos)) : pState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
        pBuilder.add(AMOUNT);
        pBuilder.add(SUPPORT);
        pBuilder.add(LIQUID);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack kettleStack = new ItemStack(ModItems.KETTLE.get());
        kettleStack.set(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY);
        return kettleStack;
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
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, ModBlockEntities.KETTLE.get(), KettleEntity::animationTick) : createTickerHelper(pBlockEntityType, ModBlockEntities.KETTLE.get(), KettleEntity::tick);
    }
}
