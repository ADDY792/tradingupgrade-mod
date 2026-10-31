package com.tradingupgrade.block;

import com.mojang.serialization.MapCodec;
import com.tradingupgrade.block.entity.ATMBlockEntity;
import com.tradingupgrade.item.CreditCardItem;
import com.tradingupgrade.item.DebitCardItem;
import com.tradingupgrade.menu.ATMMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ATMBlock extends Block implements EntityBlock {

    public static final MapCodec<ATMBlock> CODEC = simpleCodec(ATMBlock::new);

    public ATMBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends ATMBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                               BlockPos pos, Player player, InteractionHand hand,
                                               BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            ItemStack heldItem = player.getItemInHand(hand);

            // Check if player is holding a bank card
            if (heldItem.getItem() instanceof DebitCardItem) {
                openATMMenu(player, pos, "debit");
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else if (heldItem.getItem() instanceof CreditCardItem) {
                openATMMenu(player, pos, "credit");
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            } else {
                player.sendSystemMessage(Component.translatable("message.tradingupgrade.atm.no_card"));
                return ItemInteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.translatable("message.tradingupgrade.atm.hold_card"));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void openATMMenu(Player player, BlockPos pos, String cardType) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(
                    new SimpleMenuProvider(
                            (containerId, playerInventory, p) ->
                                    new ATMMenu(containerId, playerInventory, cardType),
                            Component.translatable("container.tradingupgrade.atm")
                    ),
                    buf -> {
                        buf.writeBlockPos(pos);
                        buf.writeUtf(cardType);
                    }
            );
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ATMBlockEntity(pos, state);
    }
}
