package com.tradingupgrade.item;

import com.tradingupgrade.TradingUpgrade;
import com.tradingupgrade.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TradingUpgrade.MOD_ID);

    // Block Items
    public static final DeferredItem<BlockItem> CONVERSION_TABLE_ITEM =
            ITEMS.register("conversion_table", () -> new BlockItem(
                    ModBlocks.CONVERSION_TABLE.get(),
                    new Item.Properties()
            ));

    public static final DeferredItem<BlockItem> ATM_ITEM =
            ITEMS.register("atm", () -> new BlockItem(
                    ModBlocks.ATM.get(),
                    new Item.Properties()
            ));

    // Currency
    public static final DeferredItem<Item> DOLLAR =
            ITEMS.register("dollar", () -> new DollarItem(
                    new Item.Properties().stacksTo(64)
            ));

    // Bank Cards
    public static final DeferredItem<Item> DEBIT_CARD =
            ITEMS.register("debit_card", () -> new DebitCardItem(
                    new Item.Properties().stacksTo(1)
            ));

    public static final DeferredItem<Item> CREDIT_CARD =
            ITEMS.register("credit_card", () -> new CreditCardItem(
                    new Item.Properties().stacksTo(1)
            ));

    // Creative Tab
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TradingUpgrade.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TRADING_UPGRADE_TAB =
            CREATIVE_TABS.register("trading_upgrade_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creative_tab.tradingupgrade"))
                    .icon(() -> new ItemStack(DOLLAR.get()))
                    .displayItems((params, output) -> {
                        output.accept(CONVERSION_TABLE_ITEM.get());
                        output.accept(ATM_ITEM.get());
                        output.accept(DOLLAR.get());
                        output.accept(DEBIT_CARD.get());
                        output.accept(CREDIT_CARD.get());
                    })
                    .build()
            );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
