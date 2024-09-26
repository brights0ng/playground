package net.brights0ng.playground.registries;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.brights0ng.playground.definitions.Perm;
import net.brights0ng.playground.utilClasses.PlayerUtils;
import net.brights0ng.playground.main.State;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ChunkPos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClaimRegistry {
    public static Map<ChunkPos, String> chunkClaimMap = new HashMap<>();

    public static void claimChunk (String stateID, ChunkPos chunkPos){
        chunkClaimMap.put(chunkPos, stateID);
    }

    public static void delChunk (String stateID, ChunkPos chunkPos){
        chunkClaimMap.remove(stateID,chunkPos);
    }

    public static String getStateID(ChunkPos chunkPos){
        return chunkClaimMap.get(chunkPos);
    }

    public static State getState(ChunkPos chunkPos){
        return StateRegistry.getStateMap().get(getStateID(chunkPos));
    }

    public static boolean testChunk(ChunkPos chunkPos){
        return chunkClaimMap.containsKey(chunkPos);
    }

    public static void registerProtections() {
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            ChunkPos chunkPos = new ChunkPos(pos);
            if (testChunk(chunkPos)){
                return getState(chunkPos).canPerformAction(player.getUuid(), Perm.BREAK_BLOCK);
            } else {
                return true;
            }
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (hitResult == null || hitResult.getBlockPos() == null || !(hitResult.getType() == HitResult.Type.BLOCK)) {
                return ActionResult.PASS; // Allow interaction if no block is hit
            }
//            if (player instanceof ServerPlayerEntity serverPlayer) {
//                PlayerUtils.sendMessage(serverPlayer, "Client error!", "RED");
//                return ActionResult.FAIL;
//            } // disables interaction on client side
            ChunkPos chunkPos = new ChunkPos(hitResult.getBlockPos());
            Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();

            if (testChunk(chunkPos) && !player.isSneaking() && !BlockRegistry.universalBlocks.contains(block)){
                if(BlockRegistry.redstoneBlocks.contains(block) &&
                        !getState(chunkPos).canPerformAction(player.getUuid(),Perm.USE_REDSTONE)){
//                    PlayerUtils.sendMessage((ServerPlayerEntity) player, "You cannot use this redstone block!", "RED");
                    return ActionResult.FAIL;
                } else if(BlockRegistry.useBlocks.contains(block) &&
                        !getState(chunkPos).canPerformAction(player.getUuid(),Perm.USE_BLOCK)){
//                    PlayerUtils.sendMessage((ServerPlayerEntity) player, "You cannot use this block!", "RED");
                    return ActionResult.FAIL;
                } else if(BlockRegistry.inventoryBlocks.contains(block) &&
                        !getState(chunkPos).canPerformAction(player.getUuid(),Perm.USE_INVENTORY)){
//                    PlayerUtils.sendMessage((ServerPlayerEntity) player, "You cannot use this inventory block!", "RED");
                    return ActionResult.FAIL;
                } else if(!getState(chunkPos).canPerformAction(player.getUuid(),Perm.PLACE_BLOCK)){
//                    PlayerUtils.sendMessage((ServerPlayerEntity) player, "You cannot place blocks here!", "RED");
                    return ActionResult.FAIL;
                }
            } else if(testChunk(chunkPos) && player.isSneaking()){
                if(getState(chunkPos).canPerformAction(player.getUuid(),Perm.PLACE_BLOCK)) {
                    return ActionResult.PASS;
                } else {
                    PlayerUtils.sendMessage((ServerPlayerEntity) player, "You cannot place blocks here!", "RED");
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ChunkPos chunkPos = new ChunkPos(entity.getBlockPos());
            if (testChunk(chunkPos)) {
                if (!getState(chunkPos).canPerformAction(player.getUuid(),Perm.USE_ENTITIES)){
                    PlayerUtils.sendMessage((ServerPlayerEntity) player, "You cannot use entities here!", "RED");
                    return ActionResult.FAIL;
                } else {
                    return ActionResult.PASS;
                }

            } else {
                return ActionResult.PASS;
            }
        });
    }



    public static void saveClaimData(MinecraftServer server) {
        try {
            File worldFolder = server.getOverworld().getServer().getSavePath(WorldSavePath.ROOT).toFile();
            File dataFolder = new File(worldFolder, "imperium");
            File saveFile = new File(dataFolder, "claim_data.json");
            if (!dataFolder.exists()){
                System.out.println("Imperium folder not found, creating new folder.");
                System.out.println("Attempting to create directory at: " + dataFolder.getAbsolutePath());
                dataFolder.mkdir();
            }
            if(!saveFile.exists()){
                System.out.println("claim_data.json not found, creating new file.");
                System.out.println("Attempting to create file at: " + saveFile.getAbsolutePath());
                saveFile.createNewFile();
            }

            Gson gson = new GsonBuilder()
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
            gson.toJson(chunkClaimMap, writer);
            writer.close();
            System.out.println("Claim data saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadClaimData(MinecraftServer server) {
        try {
            File worldFolder = server.getOverworld().getServer().getSavePath(WorldSavePath.ROOT).toFile();
            File dataFolder = new File(worldFolder, "imperium");
            File saveFile = new File(dataFolder, "claim_data.json");

            // Check if the file exists before trying to read it
            if (!saveFile.exists()) {
                System.out.println("Claim data file not found, loading defaults.");
                return; // Exit if the file doesn't exist
            }

            // Create a Gson instance with custom serializers/deserializers
            Gson gson = new GsonBuilder()
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
            chunkClaimMap = gson.fromJson(reader, type);

            reader.close(); // Close the reader
            System.out.println("Claim data loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace(); // Handle IO exceptions
        } catch (JsonSyntaxException e) {
            System.out.println("Error in JSON format. Please check the claim_data.json file.");
            e.printStackTrace();
        }
    }
}
