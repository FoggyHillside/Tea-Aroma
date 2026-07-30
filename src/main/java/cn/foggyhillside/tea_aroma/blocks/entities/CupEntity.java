package cn.foggyhillside.tea_aroma.blocks.entities;

import cn.foggyhillside.tea_aroma.blocks.SyncedBlockEntity;
import cn.foggyhillside.tea_aroma.blocks.entities.inventory.CupItemHandler;
import cn.foggyhillside.tea_aroma.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CupEntity extends SyncedBlockEntity {
    private final ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> inputHandler;

    public CupEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CUP.get(), pPos, pBlockState);
        this.inventory = this.createHandler();
        this.inputHandler = LazyOptional.of(() -> new CupItemHandler(inventory));
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Override
            protected void onContentsChanged(int slot) {
                inventoryChanged();
            }
        };
    }

    public NonNullList<ItemStack> getInventoryList() {
        NonNullList<ItemStack> list = NonNullList.withSize(2, ItemStack.EMPTY);
        list.set(0, this.inventory.getStackInSlot(0));
        list.set(1, this.inventory.getStackInSlot(1));
        return list;
    }

    public SimpleContainer getInventoryContainer() {
        SimpleContainer container;
        container = new SimpleContainer(this.inventory.getSlots() + 1);
        container.setItem(0, this.inventory.getStackInSlot(0));
        container.setItem(1, this.inventory.getStackInSlot(1));

        return container;
    }

    public void emptyInventory() {
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.inventory.setStackInSlot(1, ItemStack.EMPTY);
    }

    public ItemStack getItemStack(int i) {
        return this.inventory.getStackInSlot(i).copy();
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.inventory.deserializeNBT(pTag.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", this.inventory.serializeNBT());
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
        return !this.inventory.getStackInSlot(0).isEmpty() && !this.inventory.getStackInSlot(1).isEmpty();
    }

    public boolean addItem(ItemStack itemStack, Player player) {
        if (!this.isFull() && !itemStack.isEmpty()) {
            if (this.inventory.getStackInSlot(0).isEmpty()) {
                if (!player.isCreative()) {
                    this.inventory.setStackInSlot(0, itemStack.split(1));
                } else {
                    this.inventory.setStackInSlot(0, itemStack.copyWithCount(1));
                }
            } else {
                if (!player.isCreative()) {
                    this.inventory.setStackInSlot(1, itemStack.split(1));
                } else {
                    this.inventory.setStackInSlot(1, itemStack.copyWithCount(1));
                }
            }

            return true;
        }

        return false;
    }

    public boolean extractItem(Player player, InteractionHand hand) {
        if (!this.isEmpty() && player.getItemInHand(hand).isEmpty()) {
            ItemStack firstStack = inventory.getStackInSlot(0);
            ItemStack secondStack = inventory.getStackInSlot(1);
            if (!secondStack.isEmpty()) {
                player.setItemInHand(hand, secondStack.split(1));
            } else {
                player.setItemInHand(hand, firstStack.split(1));
            }
            return true;
        }
        return false;
    }

}
