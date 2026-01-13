/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.server;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkLoadMap;
import net.minecraft.world.chunk.ChunkStatus;
import org.jspecify.annotations.Nullable;

class MinecraftServer.1
implements ChunkLoadMap {
    private @Nullable ServerChunkLoadingManager chunkLoadingManager;
    private int spawnChunkX;
    private int spawnChunkZ;
    final /* synthetic */ int field_61873;

    MinecraftServer.1() {
        this.field_61873 = i;
    }

    @Override
    public void initSpawnPos(RegistryKey<World> world, ChunkPos spawnPos) {
        ServerWorld serverWorld = MinecraftServer.this.getWorld(world);
        this.chunkLoadingManager = serverWorld != null ? serverWorld.getChunkManager().chunkLoadingManager : null;
        this.spawnChunkX = spawnPos.x;
        this.spawnChunkZ = spawnPos.z;
    }

    @Override
    public @Nullable ChunkStatus getStatus(int x, int z) {
        if (this.chunkLoadingManager == null) {
            return null;
        }
        return this.chunkLoadingManager.getStatus(ChunkPos.toLong(x + this.spawnChunkX - this.field_61873, z + this.spawnChunkZ - this.field_61873));
    }

    @Override
    public int getRadius() {
        return this.field_61873;
    }
}
