package net.brights0ng.playground.item;

import net.brights0ng.playground.Playground;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item SMOOTH_JAZZ = registerItem("smooth_jazz", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(Playground.MOD_ID, name), item);
    }

    public static void registerModItems () {
        Playground.LOGGER.info("Registering Mod Items for " + Playground.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(SMOOTH_JAZZ);
        });
    }
}
