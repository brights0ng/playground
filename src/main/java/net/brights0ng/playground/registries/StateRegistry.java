package net.brights0ng.playground.registries;

import com.google.gson.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.brights0ng.playground.utilClasses.EventScheduler;
import net.brights0ng.playground.definitions.Role;
import net.brights0ng.playground.main.State;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import com.google.gson.reflect.TypeToken;


import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import static net.brights0ng.playground.Playground.stateRegistry;

public class StateRegistry {
    private static Map<String, State> stateMap = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static MinecraftServer server;

    public static Map<String, State> getStateMap() { // stateMap getter
        return stateMap;
    }

    public static void setStateMap(Map<String, State> stateMap) { // stateMap setter
        stateRegistry.stateMap = stateMap;
    }

    public static int registerState(CommandContext<ServerCommandSource> context, String name) { // register a new state
        ServerCommandSource source = context.getSource();

        // Create new state object and set its owner.
        State newState = new State(name);
        newState.setOwnerUUID(Objects.requireNonNull(source.getPlayer()).getUuid());
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

    public static State getStateByPlayerUUID(UUID playerUUID) { // returns state of requested player
        for (State state : stateMap.values()) {
            if (state.getCitizens().contains(playerUUID)) {
                return state;
            }
        }
        return null; // Return null if the player is not a citizen of any state
    }

    public static void saveStateData(MinecraftServer server) {
        try {
            File worldFolder = server.getOverworld().getServer().getSavePath(WorldSavePath.ROOT).toFile();
            File dataFolder = new File(worldFolder, "imperium");
            File saveFile = new File(dataFolder, "state_data.json");
            if (!dataFolder.exists()){
                System.out.println("Imperium folder not found, creating new folder.");
                System.out.println("Attempting to create directory at: " + dataFolder.getAbsolutePath());
                dataFolder.mkdir();
            }
            if(!saveFile.exists()){
                System.out.println("state_data.json not found, creating new file.");
                System.out.println("Attempting to create file at: " + saveFile.getAbsolutePath());
                saveFile.createNewFile();
            }

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(UUID.class, (JsonSerializer<UUID>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                    .registerTypeAdapter(UUID.class, (JsonDeserializer<UUID>) (json, typeOfT, context) -> UUID.fromString(json.getAsString()))
                    .registerTypeAdapter(ChunkPos.class, (JsonSerializer<ChunkPos>) (src, typeOfSrc, context) -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("x", src.x);
                        jsonObject.addProperty("z", src.z);
                        return jsonObject;
                    })
                    .registerTypeAdapter(ChunkPos.class, (JsonDeserializer<ChunkPos>) (json, typeOfT, context) -> {
                        int x = json.getAsJsonObject().get("x").getAsInt();
                        int z = json.getAsJsonObject().get("z").getAsInt();
                        return new ChunkPos(x, z);
                    })
                    .create();

            FileWriter writer = new FileWriter(saveFile);
            gson.toJson(stateMap, writer);
            writer.close();
            System.out.println("State data saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadStateData(MinecraftServer server) {
        try {
            File worldFolder = server.getOverworld().getServer().getSavePath(WorldSavePath.ROOT).toFile();
            File dataFolder = new File(worldFolder, "imperium");
            File saveFile = new File(dataFolder, "state_data.json");

            // Check if the file exists before trying to read it
            if (!saveFile.exists()) {
                System.out.println("State data file not found, loading defaults.");
                return; // Exit if the file doesn't exist
            }

            // Create a Gson instance with custom serializers/deserializers
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(UUID.class, (JsonSerializer<UUID>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                    .registerTypeAdapter(UUID.class, (JsonDeserializer<UUID>) (json, typeOfT, context) -> UUID.fromString(json.getAsString()))
                    .registerTypeAdapter(ChunkPos.class, (JsonSerializer<ChunkPos>) (src, typeOfSrc, context) -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("x", src.x);
                        jsonObject.addProperty("z", src.z);
                        return jsonObject;
                    })
                    .registerTypeAdapter(ChunkPos.class, (JsonDeserializer<ChunkPos>) (json, typeOfT, context) -> {
                        int x = json.getAsJsonObject().get("x").getAsInt();
                        int z = json.getAsJsonObject().get("z").getAsInt();
                        return new ChunkPos(x, z);
                    })
                    .create();

            // Read from the file
            FileReader reader = new FileReader(saveFile);

            // Deserialize into the stateMap
            Type type = new TypeToken<Map<String, State>>(){}.getType();
            stateMap = gson.fromJson(reader, type);

            reader.close(); // Close the reader
            System.out.println("State data loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exceptions
        } catch (JsonSyntaxException e) {
            System.out.println("Error in JSON format. Please check the state_data.json file.");
            e.printStackTrace();
        }
    }

    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register(
                    CommandManager.literal("state")
                            .then(CommandManager.literal("test") // Sub-command /state test
                                    .executes(context -> {
                                        ServerCommandSource source = context.getSource();
                                        source.sendFeedback(() -> Text.literal("Test command executed!"), false);
                                        return 1;
                                    })
                            )
                            .then(CommandManager.literal("register") // register new state
                                        .then(CommandManager.argument("name", StringArgumentType.string()) // <name> argument
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
                            ) // get info on player's state
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
                            ) // claim chunk for state
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
                                                source.sendFeedback(()->Text.literal("This chunk is already claimed by " + ClaimRegistry.getStateID(chunkPos)), false);
                                            }
                                            return 1;
                                        })
                            )
                            .then(CommandManager.literal("admin")
                                    .then(CommandManager.literal("savedata")
                                            .executes(context -> {
                                                saveStateData(context.getSource().getServer());
                                                return 1;
                                            })
                                    )
                                    .then(CommandManager.literal("loaddata")
                                            .executes(context -> {
                                                loadStateData(context.getSource().getServer());
                                                return 1;
                                            })
                                    )
                                    .then(CommandManager.literal("getdata")
                                            .executes(context -> {
                                                ServerCommandSource source = context.getSource(); // Get the command source (the player or console)
                                                source.sendFeedback(() -> Text.literal("[Imperium] stateMap data: " + stateMap.toString()), false);
                                                return 1;
                                            })
                                                    .then(CommandManager.argument("state", StringArgumentType.string())
                                                            .executes(context -> {
                                                                ServerCommandSource source = context.getSource(); // Get the command source (the player or console)
                                                                String name = StringArgumentType.getString(context,"state");
                                                                State state = stateMap.get(name);
//                                                                String owner = PlayerUtils.getPlayerNameFromUUID(server,state.getOwnerUUID());
                                                                source.sendFeedback(() -> Text.literal("State not found."), false);
//                                                                List<String> citizens = new ArrayList<>();
//                                                                for(UUID citizenUUID : state.getCitizens()){
//                                                                    citizens.add((server.getPlayerManager().getPlayer(citizenUUID)).getName().getString());
//                                                                }

                                                                source.sendFeedback(() -> Text.literal("[Imperium] " + state.getStateID() + " data: "), false);
                                                                source.sendFeedback(() -> Text.literal("Owner - " + state.getOwnerUUID()), false);
                                                                source.sendFeedback(() -> Text.literal("Chunks - " + state.getClaimedChunks()), false);
//                                                                source.sendFeedback(() -> Text.literal("Citizens - " + citizens), false);

                                                                return 1;
                                                            })
                                                    )
                                    )
                            )
                            .then(CommandManager.literal("roles")
                                    .then(CommandManager.literal("set")
                                            .then(CommandManager.argument("role", StringArgumentType.string())
                                                    .executes(context -> {
                                                        ServerCommandSource source = context.getSource();
                                                        String roleString = StringArgumentType.getString(context,"role");
                                                        UUID player = Objects.requireNonNull(source.getPlayer()).getUuid();
                                                        State state = getStateByPlayerUUID(player);
                                                        Role role = state.getRoleName(roleString);

                                                        if(role == null){
                                                            source.sendFeedback(()->Text.literal("Role not found."), false);
                                                            return 1;
                                                        } else {
                                                            state.assignRole(player, role);
                                                            source.sendFeedback(()->Text.literal("Role set to " + roleString), false);
                                                            return 1;
                                                        }
                                                    })
                                            )
                                    )
                                    .then(CommandManager.literal("get")
                                            .executes(context ->{
                                                ServerCommandSource source = context.getSource();
                                                UUID player = Objects.requireNonNull(source.getPlayer()).getUuid();
                                                State state = getStateByPlayerUUID(player);
                                                Role role = Objects.requireNonNull(state).getRoleUUID(player);
                                                System.out.println("0");
                                                source.sendFeedback(()->Text.literal("Role: " + role.getPerms()), false);
                                                System.out.println("3");

                                                return 1;
                                            })
                                    )
                            )
                            .then(CommandManager.literal("border")
                                    .executes(context -> {
                                        ServerCommandSource source = context.getSource();
                                        ServerPlayerEntity player = source.getPlayer();
                                        UUID playerUUID = Objects.requireNonNull(player).getUuid();
                                        State state = getStateByPlayerUUID(playerUUID);
                                        EventScheduler scheduler = new EventScheduler();

                                        scheduler.borderTask(state, player);

                                        return 1;
                                    })

                            )


            );
        });
    }
}
