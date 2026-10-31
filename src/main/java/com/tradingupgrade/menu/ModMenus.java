package com.tradingupgrade.menu;

import com.tradingupgrade.TradingUpgrade;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, TradingUpgrade.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ConversionTableMenu>> CONVERSION_TABLE_MENU =
            MENUS.register("conversion_table_menu",
                    () -> IMenuTypeExtension.create(ConversionTableMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ATMMenu>> ATM_MENU =
            MENUS.register("atm_menu",
                    () -> IMenuTypeExtension.create(ATMMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
