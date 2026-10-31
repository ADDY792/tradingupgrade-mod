package com.tradingupgrade.block;

import com.tradingupgrade.TradingUpgrade;
import com.tradingupgrade.block.entity.ATMBlockEntity;
import com.tradingupgrade.block.entity.ConversionTableBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(TradingUpgrade.MOD_ID);

    public static final DeferredBlock<Block> CONVERSION_TABLE =
            BLOCKS.register("conversion_table", () -> new ConversionTableBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .strength(2.5f)
                            .requiresCorrectToolForDrops()
            ));

    public static final DeferredBlock<Block> ATM =
            BLOCKS.register("atm", () -> new ATMBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(5.0f)
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
            ));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
