package com.tradingupgrade.data;

import com.tradingupgrade.TradingUpgrade;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TradingUpgrade.MOD_ID);

    // Debit card balance (stored on the item)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CARD_BALANCE =
            DATA_COMPONENTS.register("card_balance",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.INT)
                            .build());

    // Credit card debt (stored on the item)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CARD_DEBT =
            DATA_COMPONENTS.register("card_debt",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.INT)
                            .build());

    // Credit card credit limit
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CREDIT_LIMIT =
            DATA_COMPONENTS.register("credit_limit",
                    () -> DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.INT)
                            .build());

    // Last interest calculation day (game day number)
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LAST_INTEREST_DAY =
            DATA_COMPONENTS.register("last_interest_day",
                    () -> DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_LONG)
                            .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
