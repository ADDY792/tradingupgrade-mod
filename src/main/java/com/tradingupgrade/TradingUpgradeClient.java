package com.tradingupgrade;

import com.tradingupgrade.block.ModBlockEntities;
import com.tradingupgrade.block.ModBlocks;
import com.tradingupgrade.item.ModItems;
import com.tradingupgrade.menu.ModMenus;
import com.tradingupgrade.menu.screen.ATMMenuScreen;
import com.tradingupgrade.menu.screen.ConversionTableScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = TradingUpgrade.MOD_ID, dist = Dist.CLIENT)
public class TradingUpgradeClient {

    public TradingUpgradeClient(IEventBus modEventBus) {
        modEventBus.addListener(this::registerMenuScreens);
    }

    private void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.CONVERSION_TABLE_MENU.get(), ConversionTableScreen::new);
        event.register(ModMenus.ATM_MENU.get(), ATMMenuScreen::new);
    }
}
