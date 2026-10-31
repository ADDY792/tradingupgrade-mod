package com.tradingupgrade.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class DollarItem extends Item {

    public DollarItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                 List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        int count = stack.getCount();
        tooltipComponents.add(Component.translatable("tooltip.tradingupgrade.dollar", count)
                .withStyle(ChatFormatting.GREEN));
    }

    @Override
    public Component getName(ItemStack stack) {
        int count = stack.getCount();
        if (count == 1) {
            return Component.translatable("item.tradingupgrade.dollar", count);
        }
        return Component.translatable("item.tradingupgrade.dollar.plural", count);
    }
}
