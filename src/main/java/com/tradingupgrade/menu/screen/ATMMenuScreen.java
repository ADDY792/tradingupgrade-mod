package com.tradingupgrade.menu.screen;

import com.tradingupgrade.TradingUpgrade;
import com.tradingupgrade.menu.ATMMenu;
import com.tradingupgrade.network.ATMActionPacket;
import com.tradingupgrade.network.ModNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ATMMenuScreen extends AbstractContainerScreen<ATMMenu> {

    private static final ResourceLocation BG_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TradingUpgrade.MOD_ID, "textures/gui/atm.png");

    private EditBox amountBox;
    private Button actionButton1;
    private Button actionButton2;
    private Button quickBtn1;
    private Button quickBtn10;
    private Button quickBtn64;

    public ATMMenuScreen(ATMMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Amount input box
        amountBox = new EditBox(this.font, x + 50, y + 20, 76, 16,
                Component.translatable("gui.tradingupgrade.amount"));
        amountBox.setMaxLength(6);
        amountBox.setFilter(s -> s.matches("\\d*"));
        amountBox.setValue("1");
        addRenderableWidget(amountBox);

        boolean isDebit = "debit".equals(menu.getCardType());

        if (isDebit) {
            // Debit card: Deposit / Withdraw
            actionButton1 = Button.builder(
                    Component.translatable("gui.tradingupgrade.deposit"),
                    button -> sendAction(ATMActionPacket.ACTION_DEPOSIT)
            ).bounds(x + 20, y + 45, 60, 20).build();

            actionButton2 = Button.builder(
                    Component.translatable("gui.tradingupgrade.withdraw"),
                    button -> sendAction(ATMActionPacket.ACTION_WITHDRAW)
            ).bounds(x + 96, y + 45, 60, 20).build();
        } else {
            // Credit card: Borrow / Repay
            actionButton1 = Button.builder(
                    Component.translatable("gui.tradingupgrade.borrow"),
                    button -> sendAction(ATMActionPacket.ACTION_BORROW)
            ).bounds(x + 20, y + 45, 60, 20).build();

            actionButton2 = Button.builder(
                    Component.translatable("gui.tradingupgrade.repay"),
                    button -> sendAction(ATMActionPacket.ACTION_REPAY)
            ).bounds(x + 96, y + 45, 60, 20).build();
        }

        addRenderableWidget(actionButton1);
        addRenderableWidget(actionButton2);

        // Quick amount buttons
        quickBtn1 = Button.builder(Component.literal("$1"),
                button -> amountBox.setValue("1")
        ).bounds(x + 20, y + 70, 36, 14).build();

        quickBtn10 = Button.builder(Component.literal("$10"),
                button -> amountBox.setValue("10")
        ).bounds(x + 60, y + 70, 36, 14).build();

        quickBtn64 = Button.builder(Component.literal("$64"),
                button -> amountBox.setValue("64")
        ).bounds(x + 100, y + 70, 36, 14).build();

        addRenderableWidget(quickBtn1);
        addRenderableWidget(quickBtn10);
        addRenderableWidget(quickBtn64);
    }

    private void sendAction(int action) {
        int amount;
        try {
            amount = Integer.parseInt(amountBox.getValue());
        } catch (NumberFormatException e) {
            return;
        }
        if (amount <= 0) return;
        ModNetworking.sendToServer(new ATMActionPacket(action, amount));
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Draw dark background panel
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF2D2D2D);
        guiGraphics.fill(x + 1, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFF1A1A2E);

        // Draw header
        String header = "debit".equals(menu.getCardType())
                ? Component.translatable("gui.tradingupgrade.atm.debit").getString()
                : Component.translatable("gui.tradingupgrade.atm.credit").getString();
        guiGraphics.drawString(this.font, header, x + 60, y + 8, 0x00FF88, false);

        // Draw balance/info
        if ("debit".equals(menu.getCardType())) {
            int balance = menu.getDebitCardBalance();
            guiGraphics.drawString(this.font,
                    Component.translatable("gui.tradingupgrade.balance", balance),
                    x + 10, y + 90, 0xFFD700, false);
        } else {
            int debt = menu.getCreditCardDebt();
            int limit = menu.getCreditCardLimit();
            int available = menu.getCreditCardAvailable();

            guiGraphics.drawString(this.font,
                    Component.translatable("gui.tradingupgrade.credit_limit", limit),
                    x + 10, y + 90, 0xFFD700, false);
            guiGraphics.drawString(this.font,
                    Component.translatable("gui.tradingupgrade.credit_debt", debt),
                    x + 10, y + 100, debt > 0 ? 0xFF4444 : 0x44FF44, false);
            guiGraphics.drawString(this.font,
                    Component.translatable("gui.tradingupgrade.credit_available", available),
                    x + 10, y + 110, 0x44AAFF, false);
        }

        // Amount label
        guiGraphics.drawString(this.font,
                Component.translatable("gui.tradingupgrade.amount_label"),
                x + 10, y + 24, 0xAAAAAA, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (amountBox.isFocused()) {
            return amountBox.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (amountBox.isFocused()) {
            return amountBox.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }
}
