package com.tradingupgrade.network;

import com.tradingupgrade.TradingUpgrade;
import com.tradingupgrade.menu.ATMMenu;
import com.tradingupgrade.menu.ConversionTableMenu;
import com.tradingupgrade.menu.screen.ATMMenuScreen;
import com.tradingupgrade.menu.screen.ConversionTableScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworking {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetworking::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(TradingUpgrade.MOD_ID)
                .versioned("1.0.0");

        // Server-bound packets
        registrar.playToServer(
                ConvertItemPacket.TYPE,
                ConvertItemPacket.STREAM_CODEC,
                ConvertItemPacket::handle
        );

        registrar.playToServer(
                ATMActionPacket.TYPE,
                ATMActionPacket.STREAM_CODEC,
                ATMActionPacket::handle
        );
    }

    /**
     * Send a packet to the server (client-side helper).
     */
    public static void sendToServer(Object packet) {
        net.neoforged.neoforge.network.PacketDistributor.sendToServer(packet);
    }
}
