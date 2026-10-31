package com.tradingupgrade.menu.screen;

import com.tradingupgrade.TradingUpgrade;
import com.tradingupgrade.menu.ConversionTableMenu;
import com.tradingupgrade.network.ModNetworking;
import com.tradingupgrade.network.ConvertItemPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ConversionTableScreen extends AbstractContainerScreen<ConversionTableMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TradingUpgrade.MOD_ID, "textures/gui/conversion_table.png");

    private Button sellButton;
    private Button buyButton;

    public ConversionTableScreen(ConversionTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Sell button: convert item to dollars
        sellButton = Button.builder(
                Component.translatable("gui.tradingupgrade.sell"),
                button -> {
                    ModNetworking.sendToServer(new ConvertItemPacket(ConvertItemPacket.SELL_MODE));
                }
        ).bounds(x + 48, y + 30, 26, 16).build();
        addRenderableWidget(sellButton);

        // Buy button: convert dollars to item
        buyButton = Button.builder(
                Component.translatable("gui.tradingupgrade.buy"),
                button -> {
                    ModNetworking.sendToServer(new ConvertItemPacket(ConvertItemPacket.BUY_MODE));
                }
        ).bounds(x + 102, y + 30, 26, 16).build();
        addRenderableWidget(buyButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        // Draw background texture
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // Draw price info
        if (menu.hasInputItemPrice()) {
            int price = menu.getInputItemPrice();
            String priceText = "$" + price;
            guiGraphics.drawString(this.font, priceText, x + 75, y + 65, 0x00AA00, false);
        } else if (!menu.getSlot(0).getItem().isEmpty()) {
            guiGraphics.drawString(this.font, "N/A", x + 78, y + 65, 0xAA0000, false);
        }

        // Draw labels
        guiGraphics.drawString(this.font,
                Component.translatable("gui.tradingupgrade.item_in"),
                x + 10, y + 22, 0x404040, false);
        guiGraphics.drawString(this.font,
                Component.translatable("gui.tradingupgrade.item_out"),
                x + 120, y + 22, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
