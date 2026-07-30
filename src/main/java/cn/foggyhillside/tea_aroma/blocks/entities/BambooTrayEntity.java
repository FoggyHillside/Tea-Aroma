package cn.foggyhillside.tea_aroma.blocks.entities;

import cn.foggyhillside.tea_aroma.blocks.BambooTrayBlock;
import cn.foggyhillside.tea_aroma.blocks.SyncedBlockEntity;
import cn.foggyhillside.tea_aroma.blocks.entities.inventory.BambooTrayItemHandler;
import cn.foggyhillside.tea_aroma.config.CommonConfigs;
import cn.foggyhillside.tea_aroma.recipe.BambooTrayRecipe;
import cn.foggyhillside.tea_aroma.registry.ModBlockEntities;
import cn.foggyhillside.tea_aroma.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BambooTrayEntity extends SyncedBlockEntity {
    private final ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> inputHandler;

    private int progress = 0;

    public BambooTrayEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BAMBOO_TRAY.get(), pPos, pBlockState);
        this.inventory = this.createHandler();
        this.inputHandler = LazyOptional.of(() -> new BambooTrayItemHandler(inventory));
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            public int getSlotLimit(int slot) {
                if (slot == 0) {
                    return 16;
                } else {
                    return 8;
                }
            }

            @Override
            protected void onContentsChanged(int slot) {
                if (level != null && !level.getBlockState(getBlockPos()).getValue(BambooTrayBlock.PROCESS_TYPE).equals(0)) {
                    level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(BambooTrayBlock.PROCESS_TYPE, 0));
                }
                progress = 0;
                inventoryChanged();
            }
        };
    }



    public void playerProcess() {
        if (progress <= 1) {
            progress++;
            inventoryChanged();
        } else {
            progress = CommonConfigs.BAMBOO_TRAY_MAX_PROGRESS.get();
            inventoryChanged();
        }
    }

    public int getProgress(){
        return this.progress;
    }

    private static void spawnItem(BambooTrayEntity entity, Level level, ItemStack itemStack) {
        Direction direction = (entity.getBlockState().getValue(BambooTrayBlock.FACING)).getCounterClockWise();
        double x = entity.worldPosition.getX() + 0.5 + direction.getStepX() * 0.25;
        double y = entity.worldPosition.getY() + 1.0;
        double z = entity.worldPosition.getZ() + 0.5 + direction.getStepZ() * 0.25;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
        itemEntity.setDeltaMovement(direction.getStepX() * 0.06F, 0.2, direction.getStepZ() * 0.06F);
        level.addFreshEntity(itemEntity);
    }

    public NonNullList<ItemStack> getInventoryList() {
        NonNullList<ItemStack> list = NonNullList.withSize(2, ItemStack.EMPTY);
        list.set(0, this.inventory.getStackInSlot(0).copy());
        list.set(1, this.inventory.getStackInSlot(1).copy());
        return list;
    }

    public SimpleContainer getInventoryContainer() {
        SimpleContainer container;
        container = new SimpleContainer(this.inventory.getSlots());
        container.setItem(0, this.inventory.getStackInSlot(0).copy());
        container.setItem(1, this.inventory.getStackInSlot(1).copy());

        return container;
    }

    public void emptyInventory() {
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.inventory.setStackInSlot(1, ItemStack.EMPTY);
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.inventory.deserializeNBT(pTag.getCompound("inventory"));
        this.progress = pTag.getInt("progress");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", this.inventory.serializeNBT());
        pTag.putInt("progress", this.progress);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap.equals(ForgeCapabilities.ITEM_HANDLER) ? this.inputHandler.cast() : super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.inputHandler.invalidate();
    }

    public boolean isEmpty() {
        return this.inventory.getStackInSlot(0).isEmpty() && this.inventory.getStackInSlot(1).isEmpty();
    }

    public boolean isFull() {
        return !(this.inventory.getStackInSlot(0).getCount() < inventory.getSlotLimit(0))
                && !(this.inventory.getStackInSlot(1).getCount() < inventory.getSlotLimit(1));
    }

    public boolean isInValidProportion() {
        if (!this.inventory.getStackInSlot(1).isEmpty()) {
            if (this.inventory.getStackInSlot(1).is(ModTags.BAMBOO_TRAY_FLOWER_SMALL)) {
                return this.inventory.getStackInSlot(1).getCount() * 2 >= this.getInventory().getStackInSlot(0).getCount();
            } else if (this.inventory.getStackInSlot(1).is(ModTags.BAMBOO_TRAY_FLOWER_TALL)) {
                return this.inventory.getStackInSlot(1).getCount() * 4 >= this.inventory.getStackInSlot(0).getCount();
            }
            return false;
        }
        return true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BambooTrayEntity entity) {
        SimpleContainer container = entity.getInventoryContainer();
        Optional<BambooTrayRecipe> recipe = level.getRecipeManager().getRecipeFor(BambooTrayRecipe.Type.INSTANCE, container, level);

        if (recipe.isPresent()) {
            if (recipe.get().getProcessType() == 2) {
                if (!state.getValue(BambooTrayBlock.PROCESS_TYPE).equals(2) && entity.isInValidProportion()) {
                    level.setBlockAndUpdate(pos, state.setValue(BambooTrayBlock.PROCESS_TYPE, 2));
                }
            } else if (recipe.get().getProcessType() == 1) {
                if (!state.getValue(BambooTrayBlock.PROCESS_TYPE).equals(1)) {
                    level.setBlockAndUpdate(pos, state.setValue(BambooTrayBlock.PROCESS_TYPE, 1));
                }
                entity.progress++;
                setChanged(level, pos, state);
            } else {
                if (!state.getValue(BambooTrayBlock.PROCESS_TYPE).equals(3)) {
                    level.setBlockAndUpdate(pos, state.setValue(BambooTrayBlock.PROCESS_TYPE, 3));
                }
                entity.progress++;
                setChanged(level, pos, state);
            }
            if (entity.progress >= CommonConfigs.BAMBOO_TRAY_MAX_PROGRESS.get()) {
                spawnItem(entity, level, recipe.get().getResultItem(level.registryAccess()).copyWithCount(entity.inventory.getStackInSlot(0).getCount()));
                if (state.getValue(BambooTrayBlock.PROCESS_TYPE).equals(2)) {
                    if (entity.inventory.getStackInSlot(1).is(ModTags.BAMBOO_TRAY_FLOWER_SMALL)) {
                        int count = entity.inventory.getStackInSlot(0).getCount() % 2 == 0 ? entity.inventory.getStackInSlot(0).getCount() / 2 : (entity.inventory.getStackInSlot(0).getCount() / 2) + 1;
                        entity.inventory.getStackInSlot(1).shrink(count);
                        setChanged(level, pos, state);
                    } else if (entity.inventory.getStackInSlot(1).is(ModTags.BAMBOO_TRAY_FLOWER_TALL)) {
                        int count = entity.inventory.getStackInSlot(0).getCount() % 4 == 0 ? entity.inventory.getStackInSlot(0).getCount() / 4 : (entity.inventory.getStackInSlot(0).getCount() / 4) + 1;
                        entity.inventory.getStackInSlot(1).shrink(count);
                        setChanged(level, pos, state);
                    }
                }
                entity.inventory.setStackInSlot(0, ItemStack.EMPTY);
                entity.progress = 0;
                setChanged(level, pos, state);
                level.setBlockAndUpdate(pos, state.setValue(BambooTrayBlock.PROCESS_TYPE, 0));
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        } else {
            if (entity.progress != 0) {
                entity.progress = 0;
                setChanged(level, pos, state);
            }
            if (!state.getValue(BambooTrayBlock.PROCESS_TYPE).equals(0)) {
                level.setBlockAndUpdate(pos, state.setValue(BambooTrayBlock.PROCESS_TYPE, 0));
            }
        }
    }

    public boolean addItem(ItemStack itemStack, Player player) {
        int slot;
        if (inventory.getStackInSlot(0).getCount() < inventory.getSlotLimit(0) && itemStack.is(ModTags.BAMBOO_TRAY_TEA) && (inventory.getStackInSlot(0).isEmpty() || itemStack.getItem().equals(inventory.getStackInSlot(0).getItem()))) {
            slot = 0;
        } else if (inventory.getStackInSlot(1).getCount() < inventory.getSlotLimit(1) && itemStack.is(ModTags.BAMBOO_TRAY_FLOWER) && (inventory.getStackInSlot(1).isEmpty() || itemStack.getItem().equals(inventory.getStackInSlot(1).getItem()))) {
            slot = 1;
        } else {
            return false;
        }
        int count = inventory.getSlotLimit(slot) - inventory.getStackInSlot(slot).getCount();
        if (itemStack.getCount() > count) {
            boolean flag = player.getAbilities().instabuild;
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            if (flag) {
                inventory.setStackInSlot(slot, itemStack.copyWithCount(inventory.getSlotLimit(slot)));
            } else {
                inventory.setStackInSlot(slot, itemStack.split(inventory.getSlotLimit(slot)));
            }
        } else {
            boolean flag = player.getAbilities().instabuild;
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            if (flag) {
                inventory.setStackInSlot(slot, itemStack.copyWithCount(inventory.getStackInSlot(slot).getCount() + itemStack.getCount()));
            } else {
                inventory.setStackInSlot(slot, itemStack.split(inventory.getStackInSlot(slot).getCount() + itemStack.getCount()));
            }
        }
        return true;
    }
}
