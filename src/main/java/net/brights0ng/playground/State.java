package net.brights0ng.playground;

import net.minecraft.util.math.ChunkPos;

import java.security.Permissions;
import java.util.*;

import static net.brights0ng.playground.Playground.claimRegistry;

public class State {
    private final String stateID;
    private Set<UUID> citizens;
    private UUID owner;
    private Map<String, Permissions> statePermissions;
    private Set<ChunkPos> claimedChunks;

    public State(String stateID) {
        this.stateID = stateID;
        this.citizens = new HashSet<>();
        this.statePermissions = new HashMap<>();
        this.claimedChunks = new HashSet<>();


    }

    public void addChunk(ChunkPos chunkPos){
        if (!ClaimRegistry.testChunk(chunkPos)){
            ClaimRegistry.claimChunk(this.stateID,chunkPos);
            claimedChunks.add(chunkPos);
        }
    }

    public void delChunk(ChunkPos chunkPos){
        if (claimedChunks.contains(chunkPos)){
            claimRegistry.delChunk(this.stateID,chunkPos);
        }
    }

    public Set<ChunkPos> getClaimedChunks(){
        return this.claimedChunks;
    }

    public void setOwner(UUID newOwner){
        this.owner = newOwner;
    }

    public UUID getOwner(){
        return this.owner;
    }

    public String getStateID(){
        return this.stateID;
    }

    public Set<UUID> getCitizens(){
        return this.citizens;
    }

    public void addCitizen(UUID citizen){
        this.citizens.add(citizen);
    }

    public void delCitizen(UUID citizen){
        this.citizens.remove(citizen);
    }

}
