package com.tradingupgrade.block.entity;

import com.tradingupgrade.block.ModBlockEntities;
import com.tradingupgrade.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ConversionTableBlockEntity extends BlockEntity {

    // Slot indices
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int DOLLAR_INPUT_SLOT = 2;
    public static final int DOLLAR_OUTPUT_SLOT = 3;

    // Item price map: item registry name -> dollar value
    private static final Map<String, Integer> ITEM_PRICES = new HashMap<>();

    static {
        // Ores & Minerals
        ITEM_PRICES.put("minecraft:diamond", 100);
        ITEM_PRICES.put("minecraft:emerald", 80);
        ITEM_PRICES.put("minecraft:gold_ingot", 30);
        ITEM_PRICES.put("minecraft:iron_ingot", 20);
        ITEM_PRICES.put("minecraft:copper_ingot", 5);
        ITEM_PRICES.put("minecraft:coal", 3);
        ITEM_PRICES.put("minecraft:redstone", 2);
        ITEM_PRICES.put("minecraft:lapis_lazuli", 5);
        ITEM_PRICES.put("minecraft:quartz", 8);
        ITEM_PRICES.put("minecraft:netherite_ingot", 500);
        ITEM_PRICES.put("minecraft:raw_iron", 10);
        ITEM_PRICES.put("minecraft:raw_gold", 15);
        ITEM_PRICES.put("minecraft:raw_copper", 3);

        // Gems & Blocks
        ITEM_PRICES.put("minecraft:diamond_block", 900);
        ITEM_PRICES.put("minecraft:emerald_block", 720);
        ITEM_PRICES.put("minecraft:gold_block", 270);
        ITEM_PRICES.put("minecraft:iron_block", 180);

        // Food
        ITEM_PRICES.put("minecraft:wheat", 1);
        ITEM_PRICES.put("minecraft:potato", 1);
        ITEM_PRICES.put("minecraft:carrot", 1);
        ITEM_PRICES.put("minecraft:beetroot", 1);
        ITEM_PRICES.put("minecraft:apple", 3);
        ITEM_PRICES.put("minecraft:beef", 4);
        ITEM_PRICES.put("minecraft:porkchop", 4);
        ITEM_PRICES.put("minecraft:chicken", 3);
        ITEM_PRICES.put("minecraft:mutton", 3);
        ITEM_PRICES.put("minecraft:cod", 3);
        ITEM_PRICES.put("minecraft:salmon", 4);
        ITEM_PRICES.put("minecraft:bread", 5);
        ITEM_PRICES.put("minecraft:golden_apple", 50);
        ITEM_PRICES.put("minecraft:enchanted_golden_apple", 300);

        // Materials
        ITEM_PRICES.put("minecraft:stick", 1);
        ITEM_PRICES.put("minecraft:leather", 5);
        ITEM_PRICES.put("minecraft:string", 2);
        ITEM_PRICES.put("minecraft:feather", 2);
        ITEM_PRICES.put("minecraft:bone", 2);
        ITEM_PRICES.put("minecraft:ender_pearl", 20);
        ITEM_PRICES.put("minecraft:blaze_rod", 30);
        ITEM_PRICES.put("minecraft:ghast_tear", 40);
        ITEM_PRICES.put("minecraft:nether_star", 1000);
        ITEM_PRICES.put("minecraft:shulker_shell", 50);
        ITEM_PRICES.put("minecraft:echo_shard", 60);

        // Wood & Building
        ITEM_PRICES.put("minecraft:oak_log", 2);
        ITEM_PRICES.put("minecraft:oak_planks", 1);
        ITEM_PRICES.put("minecraft:cobblestone", 1);
        ITEM_PRICES.put("minecraft:stone", 1);
        ITEM_PRICES.put("minecraft:sand", 1);
        ITEM_PRICES.put("minecraft:glass", 2);
        ITEM_PRICES.put("minecraft:obsidian", 15);
        ITEM_PRICES.put("minecraft:ancient_debris", 200);

        // Misc
        ITEM_PRICES.put("minecraft:book", 3);
        ITEM_PRICES.put("minecraft:paper", 1);
        ITEM_PRICES.put("minecraft:slime_ball", 5);
        ITEM_PRICES.put("minecraft:honey_bottle", 8);
        ITEM_PRICES.put("minecraft:experience_bottle", 15);
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    };

    public ConversionTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CONVERSION_TABLE_BE.get(), pos, state);
    }

    /**
     * Get the dollar value of an item.
     * Returns 0 if the item has no defined price.
     */
    public static int getItemPrice(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        String key = stack.getItem().builtInRegistryHolder()
                .key().location().toString();
        return ITEM_PRICES.getOrDefault(key, 0) * stack.getCount();
    }

    /**
     * Check if an item has a defined price.
     */
    public static boolean hasPrice(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String key = stack.getItem().builtInRegistryHolder()
                .key().location().toString();
        return ITEM_PRICES.containsKey(key);
    }

    /**
     * Try to convert the item in the input slot to dollars.
     * Returns true if conversion was successful.
     */
    public boolean convertItemToDollars() {
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return false;

        int value = getItemPrice(input);
        if (value <= 0) return false;

        // Check if output slot can accept dollars
        ItemStack currentOutput = itemHandler.getStackInSlot(DOLLAR_OUTPUT_SLOT);
        if (!currentOutput.isEmpty() && !currentOutput.is(ModItems.DOLLAR.get())) {
            return false;
        }
        if (!currentOutput.isEmpty() && currentOutput.getCount() + value > 64) {
            return false;
        }

        // Perform conversion
        ItemStack dollars = new ItemStack(ModItems.DOLLAR.get(), value);
        if (currentOutput.isEmpty()) {
            itemHandler.setStackInSlot(DOLLAR_OUTPUT_SLOT, dollars);
        } else {
            currentOutput.grow(value);
        }
        itemHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        setChanged();
        return true;
    }

    /**
     * Try to convert dollars to the item in the input slot.
     * The input slot should contain the "template" item to buy.
     * Returns true if conversion was successful.
     */
    public boolean convertDollarsToItem() {
        ItemStack template = itemHandler.getStackInSlot(INPUT_SLOT);
        ItemStack dollars = itemHandler.getStackInSlot(DOLLAR_INPUT_SLOT);

        if (template.isEmpty() || dollars.isEmpty()) return false;
        if (!dollars.is(ModItems.DOLLAR.get())) return false;

        int unitPrice = getItemPrice(new ItemStack(template.getItem(), 1));
        if (unitPrice <= 0) return false;

        int availableDollars = dollars.getCount();
        int canBuy = availableDollars / unitPrice;
        if (canBuy <= 0) return false;

        // Check output slot capacity
        ItemStack currentOutput = itemHandler.getStackInSlot(OUTPUT_SLOT);
        int maxStack = template.getMaxStackSize();
        int spaceAvailable = currentOutput.isEmpty() ? maxStack : maxStack - currentOutput.getCount();
        int toBuy = Math.min(canBuy, spaceAvailable);
        if (toBuy <= 0) return false;

        // Perform conversion
        int cost = toBuy * unitPrice;
        dollars.shrink(cost);

        ItemStack output = new ItemStack(template.getItem(), toBuy);
        if (currentOutput.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT, output);
        } else {
            currentOutput.grow(toBuy);
        }

        // Consume template if it was a single item
        if (itemHandler.getStackInSlot(INPUT_SLOT).getCount() <= 1) {
            itemHandler.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        } else {
            itemHandler.getStackInSlot(INPUT_SLOT).shrink(1);
        }

        setChanged();
        return true;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public void dropContents() {
        if (level == null || level.isClientSide()) return;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Block.popResource(level, worldPosition, stack);
                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
