package com.tradingupgrade.event;

import com.tradingupgrade.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;

import java.util.Set;

/**
 * Modifies vanilla loot tables to randomly include $1-$64 dollars.
 */
public class LootEventHandler {

    // Loot tables to modify (chests that should contain dollars)
    private static final Set<String> TARGET_LOOT_TABLES = Set.of(
            "minecraft:chests/simple_dungeon",
            "minecraft:chests/abandoned_mineshaft",
            "minecraft:chests/desert_pyramid",
            "minecraft:chests/jungle_temple",
            "minecraft:chests/stronghold_corridor",
            "minecraft:chests/stronghold_crossing",
            "minecraft:chests/stronghold_library",
            "minecraft:chests/village/village_armorer",
            "minecraft:chests/village/village_butcher",
            "minecraft:chests/village/village_cartographer",
            "minecraft:chests/village/village_fisher",
            "minecraft:chests/village/village_fletcher",
            "minecraft:chests/village/village_mason",
            "minecraft:chests/village/village_shepherd",
            "minecraft:chests/village/village_tannery",
            "minecraft:chests/village/village_temple",
            "minecraft:chests/village/village_toolsmith",
            "minecraft:chests/village/village_weaponsmith",
            "minecraft:chests/woodland_mansion",
            "minecraft:chests/shipwreck_treasure",
            "minecraft:chests/buried_treasure",
            "minecraft:chests/underwater_ruin_big",
            "minecraft:chests/underwater_ruin_small",
            "minecraft:chests/pillager_outpost",
            "minecraft:chests/bastion_treasure",
            "minecraft:chests/bastion_other",
            "minecraft:chests/bastion_bridge",
            "minecraft:chests/bastion_hoglin_stable",
            "minecraft:chests/ruined_portal",
            "minecraft:chests/ancient_city",
            "minecraft:chests/end_city_treasure",
            "minecraft:chests/nether_bridge",
            "minecraft:chests/igloo_chest",
            "minecraft:chests/spawn_bonus_chest",
            "minecraft:chests/trial_chambers/reward"
    );

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation name = event.getName();
        String path = name.toString();

        if (TARGET_LOOT_TABLES.contains(path)) {
            // Add a loot entry: 1-64 dollars with 40% chance
            event.getTable().addPool(
                    net.minecraft.world.level.storage.loot.LootPool.lootPool()
                            .add(LootItem.lootTableItem(ModItems.DOLLAR.get())
                                    .apply(SetItemCountFunction.setCount(
                                            UniformGenerator.between(1, 64)
                                    ))
                                    .setWeight(1)
                            )
                            .setBonusRolls(UniformGenerator.between(0, 1))
                            .name("tradingupgrade:dollar_loot")
                            .build()
            );
        }
    }
}
