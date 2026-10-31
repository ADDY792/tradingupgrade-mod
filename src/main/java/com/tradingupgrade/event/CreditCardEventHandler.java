package com.tradingupgrade.event;

import com.tradingupgrade.data.ModAttachments;
import com.tradingupgrade.data.PlayerFinanceData;
import com.tradingupgrade.item.CreditCardItem;
import com.tradingupgrade.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelTickEvent;

/**
 * Handles credit card daily interest calculation.
 * Runs every game tick but only applies interest once per game day.
 */
public class CreditCardEventHandler {

    private static final long TICKS_PER_DAY = 24000L; // One Minecraft day

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        // Only process once per game day (at tick 0 of each day)
        long dayTime = serverLevel.getDayTime();
        if (dayTime % TICKS_PER_DAY != 0) return;

        // Process all online players
        for (ServerPlayer player : serverLevel.players()) {
            processCreditCardInterest(player);
        }
    }

    private static void processCreditCardInterest(ServerPlayer player) {
        // Find credit card in player inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof CreditCardItem) {
                int debt = CreditCardItem.getDebt(stack);
                if (debt > 0) {
                    long lastDay = CreditCardItem.getLastInterestDay(stack);
                    long currentDay = player.level().getDayTime() / TICKS_PER_DAY;

                    if (currentDay > lastDay) {
                        // Apply interest for each missed day
                        long daysElapsed = currentDay - lastDay;
                        for (long d = 0; d < daysElapsed; d++) {
                            CreditCardItem.applyDailyInterest(stack);
                        }
                        CreditCardItem.setLastInterestDay(stack, currentDay);

                        int newDebt = CreditCardItem.getDebt(stack);
                        if (newDebt > debt) {
                            player.sendSystemMessage(Component.translatable(
                                    "message.tradingupgrade.credit.interest_applied",
                                    newDebt - debt, newDebt));
                        }
                    }
                }
            }
        }
    }
}
