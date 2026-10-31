package com.tradingupgrade.menu;

import com.tradingupgrade.block.ModBlockEntities;
import com.tradingupgrade.block.entity.ConversionTableBlockEntity;
import com.tradingupgrade.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ConversionTableMenu extends AbstractContainerMenu {

    private final ConversionTableBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    // Server constructor
    public ConversionTableMenu(int containerId, Inventory playerInventory,
                                ConversionTableBlockEntity blockEntity) {
        super(ModMenus.CONVERSION_TABLE_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        IItemHandler handler = blockEntity.getItemHandler();

        // Conversion Table slots
        // Slot 0: Item Input (left side)
        addSlot(new SlotItemHandler(handler, ConversionTableBlockEntity.INPUT_SLOT, 27, 34));
        // Slot 1: Item Output (right side)
        addSlot(new SlotItemHandler(handler, ConversionTableBlockEntity.OUTPUT_SLOT, 133, 34));
        // Slot 2: Dollar Input (for buying items)
        addSlot(new SlotItemHandler(handler, ConversionTableBlockEntity.DOLLAR_INPUT_SLOT, 80, 16));
        // Slot 3: Dollar Output (from selling items)
        addSlot(new SlotItemHandler(handler, ConversionTableBlockEntity.DOLLAR_OUTPUT_SLOT, 80, 52));

        // Player inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    // Client constructor (from network data)
    public ConversionTableMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, buf));
    }

    private static ConversionTableBlockEntity getBlockEntity(Inventory inventory, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        var be = inventory.player.level().getBlockEntity(pos);
        if (be instanceof ConversionTableBlockEntity tableBE) {
            return tableBE;
        }
        throw new IllegalStateException("No ConversionTableBlockEntity at " + pos);
    }

    /**
     * Trigger item-to-dollar conversion.
     */
    public boolean convertItemToDollars() {
        return blockEntity.convertItemToDollars();
    }

    /**
     * Trigger dollar-to-item conversion.
     */
    public boolean convertDollarsToItem() {
        return blockEntity.convertDollarsToItem();
    }

    /**
     * Get the price of the item in the input slot.
     */
    public int getInputItemPrice() {
        ItemStack input = blockEntity.getItemHandler().getStackInSlot(ConversionTableBlockEntity.INPUT_SLOT);
        return ConversionTableBlockEntity.getItemPrice(input);
    }

    /**
     * Check if the input item has a defined price.
     */
    public boolean hasInputItemPrice() {
        ItemStack input = blockEntity.getItemHandler().getStackInSlot(ConversionTableBlockEntity.INPUT_SLOT);
        return ConversionTableBlockEntity.hasPrice(input);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();

            // Container slots are 0-3, player inventory 4-39
            if (index < 4) {
                // Move from container to player inventory
                if (!moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Move from player inventory to container
                // If it's a dollar, try dollar input slot first
                if (stackInSlot.is(ModItems.DOLLAR.get())) {
                    if (!moveItemStackTo(stackInSlot, 2, 3, false)) {
                        if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    if (!moveItemStackTo(stackInSlot, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, com.tradingupgrade.block.ModBlocks.CONVERSION_TABLE.get());
    }
}
