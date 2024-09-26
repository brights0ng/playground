package net.brights0ng.playground;

import net.brights0ng.playground.registries.BlockRegistry;
import net.brights0ng.playground.registries.ClaimRegistry;
import net.brights0ng.playground.registries.ImperiumRegistry;
import net.brights0ng.playground.registries.StateRegistry;
import net.brights0ng.playground.block.ModBlocks;
import net.brights0ng.playground.item.ModItemGroups;
import net.brights0ng.playground.item.ModItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
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

		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		StateRegistry.registerCommands();
		ClaimRegistry.registerProtections();
		BlockRegistry.registerBlockRegistry();
		ImperiumRegistry.registerImperium();

		// Call loadStateData when the server has fully started
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			System.out.println("Loading state data...");
			StateRegistry.loadStateData(server);
			ClaimRegistry.loadClaimData(server);
		});

		// Call saveStateData when the server stops
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			System.out.println("Saving state data...");
			StateRegistry.saveStateData(server);
			ClaimRegistry.saveClaimData(server);
		});
	}
}