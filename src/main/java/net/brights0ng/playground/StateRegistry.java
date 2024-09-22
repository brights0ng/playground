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
import java.util.UUID;

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
        newState.addCitizen(Objects.requireNonNull(source.getPlayer().getUuid()));

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

    public static State getStateByPlayerUUID(UUID playerUUID) {
        for (State state : stateMap.values()) {
            if (state.getCitizens().contains(playerUUID)) {
                return state;
            }
        }
        return null; // Return null if the player is not a citizen of any state
    }

//    public static void registerCommands(){
//        // register state command
//        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
//            dispatcher.register(
//                    CommandManager.literal("state") // The base command '/state'
//                            .then(CommandManager.literal("register") // '/state create'
//                                    .then(CommandManager.argument("name", StringArgumentType.string()) // '/state create <name>'
//                                            .executes(context -> registerState(context, StringArgumentType.getString(context, "name"))))));
//            dispatcher.register(
//                    CommandManager.literal("state")
//                            .then(CommandManager.literal("test"))
//                            .executes(context -> {
//                                ServerCommandSource source = context.getSource();
//                                source.sendFeedback(()->Text.literal("test"), false);
//                                return 1;
//                            }));
//
//        });
//    }
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
        // Base command /state
            dispatcher.register(
                    CommandManager.literal("state")
                            .then(CommandManager.literal("test") // Sub-command /state test
                                    .executes(context -> {
                                        ServerCommandSource source = context.getSource();
                                        source.sendFeedback(() -> Text.literal("Test command executed!"), false);
                                        return 1;
                                    })
                            )
                            .then(CommandManager.literal("register") // '/state create'
                                        .then(CommandManager.argument("name", StringArgumentType.string()) // '/state create <name>'
                                                .executes(context -> {
                                                    ServerCommandSource source = context.getSource();
                                                    String name = StringArgumentType.getString(context,"name");
                                                    UUID playerUUID = Objects.requireNonNull(source.getPlayer()).getUuid();
                                                    if (getStateByPlayerUUID(playerUUID) != null){
                                                        source.sendFeedback(()->Text.literal("You are already a citizen of a state!"), false);
                                                        return 1;
                                                    } else {
                                                        registerState(context, name);
                                                        source.sendFeedback(()->Text.literal("State '" + name + "' has been created!"), false);
                                                    }
                                                    return 1;
                                                })
                                        )
                            )
                            .then(CommandManager.literal("info")
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            UUID playerUUID = Objects.requireNonNull(source.getPlayer()).getUuid();
                                            State state = getStateByPlayerUUID(playerUUID);

                                            assert state != null;
                                            if (state.getStateID() != null){
                                                source.sendFeedback(()->Text.literal("-= " + state.getStateID() + " =-"), false);
                                            } else {

                                                source.sendFeedback(()->Text.literal("You are not a citizen of any state"), false);
                                            }

                                            return 1;
                                        })
                            )
                            .then(CommandManager.literal("claim")
                                    .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            State state = getStateByPlayerUUID(Objects.requireNonNull(source.getPlayer()).getUuid());
                                            ChunkPos chunkPos = source.getPlayer().getChunkPos();

                                            if(!ClaimRegistry.testChunk(chunkPos)){
                                                assert state != null;
                                                state.addChunk(chunkPos);
                                                source.sendFeedback(()->Text.literal("You have claimed the chunk at " + chunkPos.x + ", " + chunkPos.z + "."), false);
                                            } else {
                                                source.sendFeedback(()->Text.literal("This chunk is already claimed by " + ClaimRegistry.getState(chunkPos)), false);
                                            }
                                            return 1;
                                        })
                            )
            );
        });
    }
}
