package com.tradingupgrade.network;

import com.tradingupgrade.TradingUpgrade;
import com.tradingupgrade.menu.ATMMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent from client to server for ATM actions.
 */
public record ATMActionPacket(int action, int amount) implements CustomPacketPayload {

    public static final int ACTION_DEPOSIT = 0;
    public static final int ACTION_WITHDRAW = 1;
    public static final int ACTION_BORROW = 2;
    public static final int ACTION_REPAY = 3;

    public static final Type<ATMActionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TradingUpgrade.MOD_ID, "atm_action"));

    public static final StreamCodec<FriendlyByteBuf, ATMActionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, ATMActionPacket::action,
                    ByteBufCodecs.INT, ATMActionPacket::amount,
                    ATMActionPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ATMActionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (player.containerMenu instanceof ATMMenu menu) {
                switch (packet.action()) {
                    case ACTION_DEPOSIT -> {
                        boolean success = menu.depositToCard(packet.amount());
                        if (!success) {
                            player.sendSystemMessage(
                                    net.minecraft.network.chat.Component.translatable(
                                            "message.tradingupgrade.atm.deposit_failed"));
                        }
                    }
                    case ACTION_WITHDRAW -> {
                        boolean success = menu.withdrawFromCard(packet.amount());
                        if (!success) {
                            player.sendSystemMessage(
                                    net.minecraft.network.chat.Component.translatable(
                                            "message.tradingupgrade.atm.withdraw_failed"));
                        }
                    }
                    case ACTION_BORROW -> {
                        boolean success = menu.borrowFromCredit(packet.amount());
                        if (!success) {
                            player.sendSystemMessage(
                                    net.minecraft.network.chat.Component.translatable(
                                            "message.tradingupgrade.atm.borrow_failed"));
                        }
                    }
                    case ACTION_REPAY -> {
                        boolean success = menu.repayCredit(packet.amount());
                        if (!success) {
                            player.sendSystemMessage(
                                    net.minecraft.network.chat.Component.translatable(
                                            "message.tradingupgrade.atm.repay_failed"));
                        }
                    }
                }
            }
        });
    }
}
