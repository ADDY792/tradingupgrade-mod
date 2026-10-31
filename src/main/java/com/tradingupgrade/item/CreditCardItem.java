package com.tradingupgrade.item;

import com.tradingupgrade.data.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CreditCardItem extends Item {

    public static final int INITIAL_CREDIT_LIMIT = 100;
    public static final double DAILY_INTEREST_RATE = 0.05; // 5%

    public CreditCardItem(Properties properties) {
        super(properties);
    }

    /**
     * Get the current debt on this credit card.
     */
    public static int getDebt(ItemStack stack) {
        Integer debt = stack.get(ModDataComponents.CARD_DEBT.get());
        return debt != null ? debt : 0;
    }

    /**
     * Set the debt on this credit card.
     */
    public static void setDebt(ItemStack stack, int debt) {
        stack.set(ModDataComponents.CARD_DEBT.get(), Math.max(0, debt));
    }

    /**
     * Get the credit limit of this card.
     */
    public static int getCreditLimit(ItemStack stack) {
        Integer limit = stack.get(ModDataComponents.CREDIT_LIMIT.get());
        return limit != null ? limit : INITIAL_CREDIT_LIMIT;
    }

    /**
     * Set the credit limit.
     */
    public static void setCreditLimit(ItemStack stack, int limit) {
        stack.set(ModDataComponents.CREDIT_LIMIT.get(), limit);
    }

    /**
     * Get the last interest calculation day.
     */
    public static long getLastInterestDay(ItemStack stack) {
        Long day = stack.get(ModDataComponents.LAST_INTEREST_DAY.get());
        return day != null ? day : 0L;
    }

    /**
     * Set the last interest calculation day.
     */
    public static void setLastInterestDay(ItemStack stack, long day) {
        stack.set(ModDataComponents.LAST_INTEREST_DAY.get(), day);
    }

    /**
     * Get available credit (limit - debt).
     */
    public static int getAvailableCredit(ItemStack stack) {
        return Math.max(0, getCreditLimit(stack) - getDebt(stack));
    }

    /**
     * Borrow money from the credit card.
     * Returns true if successful.
     */
    public static boolean borrow(ItemStack stack, int amount) {
        if (amount <= 0) return false;
        int available = getAvailableCredit(stack);
        if (amount > available) return false;
        setDebt(stack, getDebt(stack) + amount);
        return true;
    }

    /**
     * Repay debt on the credit card.
     * Returns the actual amount repaid.
     */
    public static int repay(ItemStack stack, int amount) {
        if (amount <= 0) return 0;
        int debt = getDebt(stack);
        int toRepay = Math.min(amount, debt);
        setDebt(stack, debt - toRepay);

        // Check if fully repaid - double the credit limit
        if (getDebt(stack) == 0 && debt > 0) {
            int currentLimit = getCreditLimit(stack);
            setCreditLimit(stack, currentLimit * 2);
        }

        return toRepay;
    }

    /**
     * Apply daily interest to the debt.
     * Called once per game day.
     */
    public static void applyDailyInterest(ItemStack stack) {
        int debt = getDebt(stack);
        if (debt <= 0) return;

        // 5% daily interest, rounded up
        int interest = (int) Math.ceil(debt * DAILY_INTEREST_RATE);
        setDebt(stack, debt + interest);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                 List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int debt = getDebt(stack);
        int limit = getCreditLimit(stack);
        int available = getAvailableCredit(stack);

        tooltipComponents.add(Component.translatable("tooltip.tradingupgrade.credit_card.limit", limit)
                .withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("tooltip.tradingupgrade.credit_card.debt", debt)
                .withStyle(debt > 0 ? ChatFormatting.RED : ChatFormatting.GREEN));
        tooltipComponents.add(Component.translatable("tooltip.tradingupgrade.credit_card.available", available)
                .withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable("tooltip.tradingupgrade.credit_card.interest")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.tradingupgrade.credit_card.hint")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getDebt(stack) > 0;
    }
}
