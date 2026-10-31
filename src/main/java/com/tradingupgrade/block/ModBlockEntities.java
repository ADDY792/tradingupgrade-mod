package com.tradingupgrade.block;

import com.tradingupgrade.TradingUpgrade;
import com.tradingupgrade.block.entity.ATMBlockEntity;
import com.tradingupgrade.block.entity.ConversionTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TradingUpgrade.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConversionTableBlockEntity>>
            CONVERSION_TABLE_BE = BLOCK_ENTITIES.register("conversion_table_be",
            () -> BlockEntityType.Builder.of(
                    ConversionTableBlockEntity::new,
                    ModBlocks.CONVERSION_TABLE.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ATMBlockEntity>>
            ATM_BE = BLOCK_ENTITIES.register("atm_be",
            () -> BlockEntityType.Builder.of(
                    ATMBlockEntity::new,
                    ModBlocks.ATM.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
