package com.tradingupgrade.menu;

import com.tradingupgrade.data.ModAttachments;
import com.tradingupgrade.data.PlayerFinanceData;
import com.tradingupgrade.item.CreditCardItem;
import com.tradingupgrade.item.DebitCardItem;
import com.tradingupgrade.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ATMMenu extends AbstractContainerMenu {

    private final String cardType; // "debit" or "credit"
    private final Player player;

    // Server constructor
    public ATMMenu(int containerId, Inventory playerInventory, String cardType) {
        super(ModMenus.ATM_MENU.get(), containerId);
        this.cardType = cardType;
        this.player = playerInventory.player;

        // Player inventory (3 rows of 9) - for dollar interaction
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Player hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    // Client constructor
    public ATMMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, buf.readUtf());
    }

    public String getCardType() {
        return cardType;
    }

    // === Debit Card Operations ===

    /**
     * Deposit physical dollars from inventory into the debit card.
     */
    public boolean depositToCard(int amount) {
        if (!"debit".equals(cardType)) return false;
        if (amount <= 0) return false;

        // Find and consume dollar items from inventory
        int consumed = consumeDollarsFromInventory(amount);
        if (consumed < amount) return false;

        // Add to card balance
        ItemStack card = findHeldCard();
        if (card.isEmpty()) return false;

        DebitCardItem.addBalance(card, amount);
        return true;
    }

    /**
     * Withdraw dollars from debit card into inventory.
     */
    public boolean withdrawFromCard(int amount) {
        if (!"debit".equals(cardType)) return false;
        if (amount <= 0) return false;

        ItemStack card = findHeldCard();
        if (card.isEmpty()) return false;

        if (!DebitCardItem.withdrawBalance(card, amount)) return false;

        // Give dollar items to player
        giveDollarsToPlayer(amount);
        return true;
    }

    /**
     * Get debit card balance.
     */
    public int getDebitCardBalance() {
        ItemStack card = findHeldCard();
        if (card.isEmpty() || !(card.getItem() instanceof DebitCardItem)) return 0;
        return DebitCardItem.getBalance(card);
    }

    // === Credit Card Operations ===

    /**
     * Borrow money from credit card (gives dollars to inventory).
     */
    public boolean borrowFromCredit(int amount) {
        if (!"credit".equals(cardType)) return false;
        if (amount <= 0) return false;

        ItemStack card = findHeldCard();
        if (card.isEmpty() || !(card.getItem() instanceof CreditCardItem)) return false;

        if (!CreditCardItem.borrow(card, amount)) return false;

        giveDollarsToPlayer(amount);
        player.sendSystemMessage(Component.translatable("message.tradingupgrade.credit.borrowed", amount));
        return true;
    }

    /**
     * Repay credit card debt using dollars from inventory.
     */
    public boolean repayCredit(int amount) {
        if (!"credit".equals(cardType)) return false;
        if (amount <= 0) return false;

        int consumed = consumeDollarsFromInventory(amount);
        if (consumed < amount) return false;

        ItemStack card = findHeldCard();
        if (card.isEmpty() || !(card.getItem() instanceof CreditCardItem)) return false;

        int repaid = CreditCardItem.repay(card, amount);
        if (repaid > 0) {
            player.sendSystemMessage(Component.translatable("message.tradingupgrade.credit.repaid", repaid));
            int debt = CreditCardItem.getDebt(card);
            if (debt == 0) {
                int newLimit = CreditCardItem.getCreditLimit(card);
                player.sendSystemMessage(Component.translatable("message.tradingupgrade.credit.limit_doubled", newLimit));
            }
        }
        return repaid > 0;
    }

    /**
     * Get credit card info.
     */
    public int getCreditCardDebt() {
        ItemStack card = findHeldCard();
        if (card.isEmpty() || !(card.getItem() instanceof CreditCardItem)) return 0;
        return CreditCardItem.getDebt(card);
    }

    public int getCreditCardLimit() {
        ItemStack card = findHeldCard();
        if (card.isEmpty() || !(card.getItem() instanceof CreditCardItem)) return 0;
        return CreditCardItem.getCreditLimit(card);
    }

    public int getCreditCardAvailable() {
        ItemStack card = findHeldCard();
        if (card.isEmpty() || !(card.getItem() instanceof CreditCardItem)) return 0;
        return CreditCardItem.getAvailableCredit(card);
    }

    // === Helper Methods ===

    private ItemStack findHeldCard() {
        // Check main hand first, then off hand
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof DebitCardItem || mainHand.getItem() instanceof CreditCardItem) {
            return mainHand;
        }
        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof DebitCardItem || offHand.getItem() instanceof CreditCardItem) {
            return offHand;
        }
        // Search inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof DebitCardItem || stack.getItem() instanceof CreditCardItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private int consumeDollarsFromInventory(int amount) {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(ModItems.DOLLAR.get())) {
                int take = Math.min(stack.getCount(), remaining);
                stack.shrink(take);
                remaining -= take;
            }
        }
        return amount - remaining;
    }

    private void giveDollarsToPlayer(int amount) {
        int remaining = amount;
        while (remaining > 0) {
            int stackSize = Math.min(remaining, 64);
            ItemStack dollars = new ItemStack(ModItems.DOLLAR.get(), stackSize);
            if (!player.getInventory().add(dollars)) {
                player.drop(dollars, false);
            }
            remaining -= stackSize;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // Simplified - no shift-click in ATM
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
