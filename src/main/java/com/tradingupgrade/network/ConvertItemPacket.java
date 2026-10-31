package com.tradingupgrade.network;

import com.tradingupgrade.TradingUpgrade;
import com.tradingupgrade.menu.ConversionTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent from client to server to trigger item conversion.
 */
public record ConvertItemPacket(int mode) implements CustomPacketPayload {

    public static final int SELL_MODE = 0;  // Item -> Dollars
    public static final int BUY_MODE = 1;   // Dollars -> Item

    public static final Type<ConvertItemPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradingUpgrade.MOD_ID, "convert_item"));

    public static final StreamCodec<FriendlyByteBuf, ConvertItemPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, ConvertItemPacket::mode,
                    ConvertItemPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ConvertItemPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player.containerMenu instanceof ConversionTableMenu menu) {
                if (packet.mode() == SELL_MODE) {
                    boolean success = menu.convertItemToDollars();
                    if (!success) {
                        player.sendSystemMessage(
                                net.minecraft.network.chat.Component.translatable(
                                        "message.tradingupgrade.convert.sell_failed"));
                    }
                } else if (packet.mode() == BUY_MODE) {
                    boolean success = menu.convertDollarsToItem();
                    if (!success) {
                        player.sendSystemMessage(
                                net.minecraft.network.chat.Component.translatable(
                                        "message.tradingupgrade.convert.buy_failed"));
                    }
                }
            }
        });
    }
}
