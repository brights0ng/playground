package net.brights0ng.playground.registries;

import net.minecraft.block.*;

import net.minecraft.registry.Registries;

import java.util.HashSet;
import java.util.Set;

public class BlockRegistry {
    public static Set<Block> redstoneBlocks;
    public static Set<Block> useBlocks;
    public static Set<Block> inventoryBlocks;
    public static Set<Block> universalBlocks;

    public static void registerBlockRegistry() {
        // Initialize the sets
        redstoneBlocks = new HashSet<>();
        useBlocks = new HashSet<>();
        inventoryBlocks = new HashSet<>();
        universalBlocks = new HashSet<>();

        // Automatically add all button blocks from the block registry
        for (Block block : Registries.BLOCK) {
            if (block instanceof ButtonBlock || block instanceof TntBlock
                    || block instanceof LeverBlock) {
                redstoneBlocks.add(block);
            }
            if (block instanceof DoorBlock || block instanceof TrapdoorBlock
                    || block instanceof FenceGateBlock || block instanceof BedBlock
                    || block instanceof CakeBlock || block instanceof RespawnAnchorBlock
                    || block instanceof ScaffoldingBlock || block instanceof AbstractSignBlock) {
                useBlocks.add(block);
            }
            if (block instanceof BeehiveBlock || block instanceof BarrelBlock
                    || block instanceof ChestBlock || block instanceof ShulkerBoxBlock
                    || block instanceof AbstractFurnaceBlock || block instanceof BrewingStandBlock
                    || block instanceof AbstractCauldronBlock || block instanceof HopperBlock
                    || block instanceof DispenserBlock || block instanceof DropperBlock
                    || block instanceof ChiseledBookshelfBlock || block instanceof CrafterBlock) {
                inventoryBlocks.add(block);
            }
            if ((block instanceof CraftingTableBlock && !(block instanceof FletchingTableBlock)) || block instanceof StonecutterBlock
                    || block instanceof AbstractRailBlock || block instanceof SweetBerryBushBlock
                    || block instanceof CartographyTableBlock || block instanceof ComposterBlock
                    || block instanceof NoteBlock || block instanceof RedstoneOreBlock
                    || block instanceof EnchantingTableBlock
                    || block instanceof TrialSpawnerBlock || block instanceof EndGatewayBlock
                    || block instanceof BellBlock || block instanceof CampfireBlock
                    || block instanceof JukeboxBlock) {
                universalBlocks.add(block);
            }
        }
    }
}