package net.brights0ng.playground.utilClasses;

import net.brights0ng.playground.definitions.Coord;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.Set;

public class ChunkSide {
    public Set<Coord> northSide;
    public Set<Coord> southSide;
    public Set<Coord> eastSide;
    public Set<Coord> westSide;

    public ChunkSide(ChunkPos chunkPos) {
        northSide = new HashSet<>();
        southSide = new HashSet<>();
        eastSide = new HashSet<>();
        westSide = new HashSet<>();

        int chunkStartX = chunkPos.getStartX();
        int chunkEndX = chunkPos.getEndX();
        int chunkStartZ = chunkPos.getStartZ();
        int chunkEndZ = chunkPos.getEndZ();

        // North Side (X varies, Z is constant)
        for (int x = chunkStartX; x <= chunkEndX; x += 2) {
            northSide.add(new Coord(x, chunkStartZ));
        }

        // South Side (X varies, Z is constant)
        for (int x = chunkStartX; x <= chunkEndX; x += 2) {
            southSide.add(new Coord(x, chunkEndZ));
        }

        // East Side (Z varies, X is constant)
        for (int z = chunkStartZ; z <= chunkEndZ; z += 2) {
            eastSide.add(new Coord(chunkEndX, z));
        }

        // West Side (Z varies, X is constant)
        for (int z = chunkStartZ; z <= chunkEndZ; z += 2) {
            westSide.add(new Coord(chunkStartX, z));
        }
    }

    public static ChunkPos shiftChunkPos(String direction, ChunkPos chunkPos){
        int x;
        int z;
        if(direction.equals("north")) {
            z = chunkPos.z - 1;
            x = chunkPos.x;
            chunkPos = new ChunkPos(x, z);
        }
        if (direction.equals("south")) {
            z = chunkPos.z + 1;
            x = chunkPos.x;
            chunkPos = new ChunkPos(x, z);
        }
        if (direction.equals("east")) {
            z = chunkPos.z;
            x = chunkPos.x + 1;
            chunkPos = new ChunkPos(x, z);
        }
        if (direction.equals("west")) {
            z = chunkPos.z;
            x = chunkPos.x - 1;
            chunkPos = new ChunkPos(x, z);
        }
        return chunkPos;
    }
}
