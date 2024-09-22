package net.brights0ng.playground;

import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public class ClaimRegistry {
    public static final Map<ChunkPos, String> chunkClaimMap = new HashMap<>();

    public static void claimChunk (String stateID, ChunkPos chunkPos){
        chunkClaimMap.put(chunkPos, stateID);
    }

    public static void delChunk (String stateID, ChunkPos chunkPos){
        chunkClaimMap.remove(stateID,chunkPos);
    }


    public static String getState(ChunkPos chunkPos){
        return chunkClaimMap.get(chunkPos);
    }

    public static boolean testChunk(ChunkPos chunkPos){
        return chunkClaimMap.containsKey(chunkPos);
    }
}
