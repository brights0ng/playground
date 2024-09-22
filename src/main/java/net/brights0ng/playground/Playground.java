package net.brights0ng.playground;

import net.brights0ng.playground.block.ModBlocks;
import net.brights0ng.playground.item.ModItemGroups;
import net.brights0ng.playground.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Playground implements ModInitializer {
	public static final String MOD_ID = "playground";
	public static ClaimRegistry claimRegistry;
	public static StateRegistry stateRegistry;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		StateRegistry.registerCommands();



	}
}