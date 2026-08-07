package cn.foggyhillside.tea_aroma.blocks.entities;

import cn.foggyhillside.tea_aroma.blocks.KettleBlock;
import cn.foggyhillside.tea_aroma.blocks.entities.states.KettleLiquid;
import cn.foggyhillside.tea_aroma.component.KettleContents;
import cn.foggyhillside.tea_aroma.registry.ModBlockEntities;
import cn.foggyhillside.tea_aroma.registry.ModDataComponents;
import cn.foggyhillside.tea_aroma.registry.ModParticleTypes;
import cn.foggyhillside.tea_aroma.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class KettleEntity extends SyncedBlockEntity {
    private int boilProgress;
    private static final int MAX_PROGRESS = 200;
    private static final int BOIL = 160;
    private static final float COOLING_CHANCE = 0.01F;

    public KettleEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KETTLE.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.boilProgress = pTag.getInt("boil_progress");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putInt("boil_progress", this.boilProgress);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput pComponentInput) {
        super.applyImplicitComponents(pComponentInput);
        KettleContents contents = pComponentInput.getOrDefault(ModDataComponents.KETTLE_CONTENTS.get(), KettleContents.EMPTY);
        this.boilProgress = contents.boil_progress();
    }

    public static boolean isHeated(Level level, BlockPos pos) {
        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.is(ModTags.HEAT_SOURCES)) {
            return stateBelow.hasProperty(BlockStateProperties.LIT) ? stateBelow.getValue(BlockStateProperties.LIT) : true;
        } else {
            return false;
        }
    }

    public static void boil(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(KettleBlock.LIQUID).equals(KettleLiquid.WATER)) {
            level.setBlockAndUpdate(pos, state.setValue(KettleBlock.LIQUID, KettleLiquid.BOILING_WATER));
        } else {
            level.setBlockAndUpdate(pos, state.setValue(KettleBlock.LIQUID, KettleLiquid.BOILING_MILK));
        }
    }

    public static void cool(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(KettleBlock.LIQUID).equals(KettleLiquid.BOILING_WATER)) {
            level.setBlockAndUpdate(pos, state.setValue(KettleBlock.LIQUID, KettleLiquid.WATER));
        } else {
            level.setBlockAndUpdate(pos, state.setValue(KettleBlock.LIQUID, KettleLiquid.MILK));
        }
    }

    public static boolean isBoiling(BlockState pState) {
        return pState.getValue(KettleBlock.LIQUID) == KettleLiquid.BOILING_WATER
                || pState.getValue(KettleBlock.LIQUID) == KettleLiquid.BOILING_MILK;
    }

    public static boolean isCool(BlockState pState) {
        return pState.getValue(KettleBlock.LIQUID) == KettleLiquid.WATER
                || pState.getValue(KettleBlock.LIQUID) == KettleLiquid.MILK;
    }

    public static void animationTick(Level pLevel, BlockPos pPos, BlockState pState, KettleEntity entity) {
        if (isBoiling(pState)) {
            RandomSource random = pLevel.random;
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof KettleEntity) {
                double chance = (entity.boilProgress - BOIL) * Math.pow(MAX_PROGRESS - BOIL, -1) + random.nextDouble() - 0.5F;
                if (chance > 0) {
                    double x = pPos.getX() + 0.5;
                    double y = pPos.getY();
                    double z = pPos.getZ() + 0.5;

                    Direction direction = pState.getValue(HorizontalDirectionalBlock.FACING).getClockWise();
                    Direction.Axis axis = direction.getAxis();
                    double offset = random.nextDouble() * 0.2 - 0.1;
                    double xOffset = axis == Direction.Axis.X ? direction.getStepX() * 0.55 : offset;
                    double yOffset = random.nextDouble() * 4.0 / 16.0;
                    double zOffset = axis == Direction.Axis.Z ? direction.getStepZ() * 0.55 : offset;
                    pLevel.addParticle(ModParticleTypes.STEAM.get(), x + xOffset, y + 0.15 + yOffset, z + zOffset, 0.1, 0.1, 0.1);
                }
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, KettleEntity entity) {
        if (state.getValue(KettleBlock.LIQUID).equals(KettleLiquid.NONE) && entity.boilProgress != 0) {
            entity.boilProgress = 0;
            setChanged(level, pos, state);
        } else {
            if (entity.boilProgress > MAX_PROGRESS) {
                entity.boilProgress = MAX_PROGRESS;
                setChanged(level, pos, state);
            } else if (entity.boilProgress < 0) {
                entity.boilProgress = 0;
                setChanged(level, pos, state);
            }
        }
        if (state.getValue(KettleBlock.AMOUNT).equals(0)
                && !state.getValue(KettleBlock.LIQUID).equals(KettleLiquid.NONE)) {
            level.setBlockAndUpdate(pos, state.setValue(KettleBlock.LIQUID, KettleLiquid.NONE));
        } else if (!state.getValue(KettleBlock.AMOUNT).equals(0)
                && state.getValue(KettleBlock.LIQUID).equals(KettleLiquid.NONE)) {
            level.setBlockAndUpdate(pos, state.setValue(KettleBlock.AMOUNT, 0));
        } else if (!state.getValue(KettleBlock.LIQUID).equals(KettleLiquid.NONE)) {
            if (isCool(state)) {
                if (entity.boilProgress >= BOIL) {
                    boil(level, pos, state);
                }
            } else {
                if (entity.boilProgress < BOIL) {
                    cool(level, pos, state);
                }
            }
            if (isHeated(level, pos)) {
                if (entity.boilProgress < MAX_PROGRESS) {
                    entity.boilProgress++;
                    setChanged(level, pos, state);
                }
            } else {
                if (entity.boilProgress > 0 && level.random.nextFloat() < COOLING_CHANCE) {
                    entity.boilProgress--;
                    setChanged(level, pos, state);
                }
            }
        }
    }

    public int getBoilProgress() {
        return this.boilProgress;
    }

    public void setBoilProgress(int progress) {
        this.boilProgress = progress;
        setChanged();
    }
}
