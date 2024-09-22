package net.brights0ng.playground;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.brights0ng.playground.Playground.stateRegistry;

public class StateRegistry {
    private static Map<String, State> stateMap = new HashMap<>();

    public static Map<String, State> getStateMap() {
        return stateMap;
    }

    public static void setStateMap(Map<String, State> stateMap) {
        stateRegistry.stateMap = stateMap;
    }

    public static int registerState(CommandContext<ServerCommandSource> context, String name) {
        ServerCommandSource source = context.getSource();

        // Create new state object and set its owner.
        State newState = new State(name);
        newState.setOwner(Objects.requireNonNull(source.getPlayer()).getUuid());
        stateMap.put(name, newState);

        // Claim chunk the player is standing in
        int chunkX = source.getPlayer().getBlockPos().getX() >> 4;
        int chunkZ = source.getPlayer().getBlockPos().getZ() >> 4;
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        newState.addChunk(chunkPos); // Ensure this method exists in your State class

        // Notify the player
        source.sendFeedback(() -> Text.literal("State '" + name + "' has been created!"), false);
        source.sendFeedback(() -> Text.literal("You have claimed the chunk at " + chunkX + ", " + chunkZ + "."), false);

        return 1; // Return success
    }

    public static void registerCommands(){
        // register state command
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register(
                    CommandManager.literal("state") // The base command '/state'
                            .then(CommandManager.literal("register") // '/state create'
                                    .then(CommandManager.argument("name", StringArgumentType.string()) // '/state create <name>'
                                            .executes(context -> registerState(context, StringArgumentType.getString(context, "name"))))));
        });
    }
}
