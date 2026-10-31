package com.tradingupgrade.block;

import com.mojang.serialization.MapCodec;
import com.tradingupgrade.block.entity.ConversionTableBlockEntity;
import com.tradingupgrade.menu.ConversionTableMenu;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ConversionTableBlock extends Block implements EntityBlock {

    public static final MapCodec<ConversionTableBlock> CODEC = simpleCodec(ConversionTableBlock::new);

    public ConversionTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends ConversionTableBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ConversionTableBlockEntity tableBE) {
                ((ServerPlayer) player).openMenu(
                        new SimpleMenuProvider(
                                (containerId, playerInventory, p) ->
                                        new ConversionTableMenu(containerId, playerInventory, tableBE),
                                Component.translatable("container.tradingupgrade.conversion_table")
                        ),
                        buf -> buf.writeBlockPos(pos)
                );
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConversionTableBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ConversionTableBlockEntity tableBE) {
                tableBE.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
