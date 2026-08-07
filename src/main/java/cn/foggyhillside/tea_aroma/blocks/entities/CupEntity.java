package cn.foggyhillside.tea_aroma.blocks.entities;

import cn.foggyhillside.tea_aroma.TeaAroma;
import cn.foggyhillside.tea_aroma.blocks.entities.inventory.CupItemHandler;
import cn.foggyhillside.tea_aroma.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;

@EventBusSubscriber(modid = TeaAroma.MODID)
public class CupEntity extends SyncedBlockEntity {
    private final ItemStackHandler inventory;

    public CupEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CUP.get(), pos, state);
        this.inventory = this.createHandler();
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

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ItemStack getItemStack(int i) {
        return this.inventory.getStackInSlot(i).copy();
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.inventory.deserializeNBT(pRegistries, pTag.getCompound("inventory"));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("inventory", this.inventory.serializeNBT(pRegistries));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.CUP.get(),
                (be, context) -> new CupItemHandler(be.getInventory())
        );
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
