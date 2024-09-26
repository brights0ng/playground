package net.brights0ng.playground.main;

import net.brights0ng.playground.definitions.Coord;
import net.brights0ng.playground.definitions.DefaultRoles;
import net.brights0ng.playground.definitions.Perm;
import net.brights0ng.playground.definitions.Role;
import net.brights0ng.playground.registries.ClaimRegistry;
import net.brights0ng.playground.utilClasses.ChunkSide;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

import java.util.*;

public class State {
    private final String stateID;
    private Set<UUID> citizens;
    private UUID ownerUUID;
    private Map<UUID, Role> roleFinder;
    private Map<String, Role> roleMap;
    private Set<ChunkPos> claimedChunks;
    public Imperium imperium;

    private Role owner = DefaultRoles.createRoleOwner();
    private Role citizen = DefaultRoles.createRoleCitizen();
    private Role guest = DefaultRoles.createRoleGuest();
    private Role ally = DefaultRoles.createRoleAlly();
    private Role admin = DefaultRoles.createRoleAdmin();

    public State(String stateID) {
        this.stateID = stateID;
        this.citizens = new HashSet<>();
        this.roleFinder = new HashMap<>();
        this.claimedChunks = new HashSet<>();
        this.roleMap = new HashMap<>();

        roleMap.put("owner",owner);
        roleMap.put("citizen",citizen);
        roleMap.put("guest",guest);
        roleMap.put("ally",ally);
        roleMap.put("admin",admin);
    }

    public void addChunk(ChunkPos chunkPos){
        if (!ClaimRegistry.testChunk(chunkPos)){
            ClaimRegistry.claimChunk(this.stateID,chunkPos);
            claimedChunks.add(chunkPos);
        }
    }

    public void delChunk(ChunkPos chunkPos){
        if (claimedChunks.contains(chunkPos)){
            ClaimRegistry.delChunk(this.stateID,chunkPos);
        }
    }

    public Set<ChunkPos> getClaimedChunks(){
        return this.claimedChunks;
    }

    public void setOwnerUUID(UUID newOwner){
        this.ownerUUID = newOwner;
        assignRole(newOwner,owner);
    }

    public UUID getOwnerUUID(){
        return this.ownerUUID;
    }

    public String getStateID(){
        return this.stateID;
    }

    public Set<UUID> getCitizens(){
        return this.citizens;
    }

    public void addCitizen(UUID newCitizen){
        this.citizens.add(newCitizen);
        assignRole(newCitizen,citizen);
    }

    public void delCitizen(UUID citizen){
        this.citizens.remove(citizen);
        removeRole(citizen);
    }

    public void assignRole(UUID citizen, Role role){
        if(role != owner){
            this.roleFinder.put(citizen,role);
        }
    }

    public void removeRole(UUID citizen){
        this.roleFinder.remove(citizen);
    }

    public Role getRoleUUID(UUID citizen) {
        return this.roleFinder.get(citizen);
    }
    public Role getRoleName(String role) {
        return this.roleMap.get(role);
    }

    public Set<Role>  getRoles(){
        return new HashSet<>(this.roleMap.values());
    }

    public void addRolePerm(Role role, Perm perm) {
        if(!role.hasPerm(perm)){
            role.addPerm(perm);
        }
    }

    public void delRolePerm(Role role, Perm perm) {
        role.removePerm(perm);
    }

    public Role createRole(String role) {
        for(Role r : this.roleMap.values()){
            if(r.getRoleName().equals(role)){
                return null;
            }
        }
        Role r = new Role(role);
        addRolePerm(r,Perm.USE_REDSTONE);
        addRolePerm(r,Perm.USE_ENTITIES);
        roleMap.put(role,r);
        return r;

    }

    public void deleteRole(String role){
        roleMap.remove(role);
    }

    public boolean canPerformAction(UUID player, Perm perm) {
        return this.roleFinder.get(player).getPerms().contains(perm);
    }

    public void createBorder(ServerPlayerEntity player){
        ServerWorld world = (ServerWorld) player.getWorld();
        for (ChunkPos chunkPos : claimedChunks) {
            ChunkSide chunkSide = new ChunkSide(chunkPos);
            if (!claimedChunks.contains(ChunkSide.shiftChunkPos("north",chunkPos))){
                for(Coord coord : chunkSide.northSide){
                    DustParticleEffect  dustEffect = new DustParticleEffect(new Vector3f(0,1,1),10);
                    world.spawnParticles(dustEffect, coord.x, player.getY(), coord.z, 1, 0, 0, 0, 0);
                }
            }
            if (!claimedChunks.contains(ChunkSide.shiftChunkPos("east",chunkPos))) {
                for (Coord coord : chunkSide.eastSide) {
                    DustParticleEffect dustEffect = new DustParticleEffect(new Vector3f(1, 0, 0), 10);
                    world.spawnParticles(dustEffect, coord.x, player.getY(), coord.z, 1, 0, 0, 0, 0);
                }
            }
            if (!claimedChunks.contains(ChunkSide.shiftChunkPos("south",chunkPos))) {
                for (Coord coord : chunkSide.southSide) {
                    DustParticleEffect dustEffect = new DustParticleEffect(new Vector3f(1, 1, 1), 10);
                    world.spawnParticles(dustEffect, coord.x, player.getY(), coord.z, 1, 0, 0, 0, 0);
                }
            }
            if (!claimedChunks.contains(ChunkSide.shiftChunkPos("west",chunkPos))) {
                for (Coord coord : chunkSide.westSide) {
                    DustParticleEffect dustEffect = new DustParticleEffect(new Vector3f(1, 1, 1), 10);
                    world.spawnParticles(dustEffect, coord.x, player.getY(), coord.z, 1, 0, 0, 0, 0);
                }
            }

        }

    }
}
