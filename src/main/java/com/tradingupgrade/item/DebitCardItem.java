package com.tradingupgrade.item;

import com.tradingupgrade.data.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DebitCardItem extends Item {

    public DebitCardItem(Properties properties) {
        super(properties);
    }

    /**
     * Get the balance stored on this debit card.
     */
    public static int getBalance(ItemStack stack) {
        Integer balance = stack.get(ModDataComponents.CARD_BALANCE.get());
        return balance != null ? balance : 0;
    }

    /**
     * Set the balance on this debit card.
     */
    public static void setBalance(ItemStack stack, int balance) {
        stack.set(ModDataComponents.CARD_BALANCE.get(), balance);
    }

    /**
     * Add money to the debit card.
     */
    public static boolean addBalance(ItemStack stack, int amount) {
        if (amount <= 0) return false;
        int current = getBalance(stack);
        setBalance(stack, current + amount);
        return true;
    }

    /**
     * Withdraw money from the debit card.
     * Returns true if successful (enough balance).
     */
    public static boolean withdrawBalance(ItemStack stack, int amount) {
        if (amount <= 0) return false;
        int current = getBalance(stack);
        if (current < amount) return false;
        setBalance(stack, current - amount);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                 List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        int balance = getBalance(stack);
        tooltipComponents.add(Component.translatable("tooltip.tradingupgrade.debit_card.balance", balance)
                .withStyle(ChatFormatting.GOLD));
        tooltipComponents.add(Component.translatable("tooltip.tradingupgrade.debit_card.hint")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return getBalance(stack) > 0;
    }
}
