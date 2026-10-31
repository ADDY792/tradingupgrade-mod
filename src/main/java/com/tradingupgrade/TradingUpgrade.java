package com.tradingupgrade;

import com.tradingupgrade.data.ModAttachments;
import com.tradingupgrade.data.ModDataComponents;
import com.tradingupgrade.event.LootEventHandler;
import com.tradingupgrade.event.CreditCardEventHandler;
import com.tradingupgrade.network.ModNetworking;
import com.tradingupgrade.block.ModBlockEntities;
import com.tradingupgrade.block.ModBlocks;
import com.tradingupgrade.item.ModItems;
import com.tradingupgrade.menu.ModMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(TradingUpgrade.MOD_ID)
public class TradingUpgrade {

    public static final String MOD_ID = "tradingupgrade";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TradingUpgrade(IEventBus modEventBus) {
        LOGGER.info("Trading Upgrade mod initializing...");

        // Register all deferred registries
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenus.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModNetworking.register(modEventBus);

        // Register creative tab
        ModItems.CREATIVE_TABS.register(modEventBus);

        // Register game event handlers
        NeoForge.EVENT_BUS.register(LootEventHandler.class);
        NeoForge.EVENT_BUS.register(CreditCardEventHandler.class);

        LOGGER.info("Trading Upgrade mod initialized!");
    }
}
