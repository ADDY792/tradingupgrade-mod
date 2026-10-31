package com.tradingupgrade.data;

import com.tradingupgrade.TradingUpgrade;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TradingUpgrade.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerFinanceData>> PLAYER_FINANCE_DATA =
            ATTACHMENT_TYPES.register("player_finance_data",
                    () -> AttachmentType.builder(() -> PlayerFinanceData.DEFAULT)
                            .serialize(PlayerFinanceData.CODEC)
                            .copyOnDeath()
                            .build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
