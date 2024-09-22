package net.brights0ng.playground.item;

import net.brights0ng.playground.Playground;
import net.brights0ng.playground.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup PULCHRITUDINOUS_MUSIC = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Playground.MOD_ID,"pulchritudinous_music"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.SMOOTH_JAZZ))
                    .displayName(Text.translatable("itemgroup.playground.pulchritudinous_music"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.SMOOTH_JAZZ);
                        entries.add(ModBlocks.CONGEALED_SAXOPHONE_BLOCK);
                    })


            .build());

    public static void registerItemGroups() {
        Playground.LOGGER.info("Registering item groups for " + Playground.MOD_ID);
    }
}
