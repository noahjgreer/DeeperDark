/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
final class ClientChunkManager.ClientChunkMap {
    final AtomicReferenceArray<@Nullable WorldChunk> chunks;
    final LongOpenHashSet activeSections = new LongOpenHashSet();
    final int radius;
    private final int diameter;
    volatile int centerChunkX;
    volatile int centerChunkZ;
    int loadedChunkCount;

    ClientChunkManager.ClientChunkMap(int radius) {
        this.radius = radius;
        this.diameter = radius * 2 + 1;
        this.chunks = new AtomicReferenceArray(this.diameter * this.diameter);
    }

    int getIndex(int chunkX, int chunkZ) {
        return Math.floorMod(chunkZ, this.diameter) * this.diameter + Math.floorMod(chunkX, this.diameter);
    }

    void set(int index, @Nullable WorldChunk chunk) {
        WorldChunk worldChunk = this.chunks.getAndSet(index, chunk);
        if (worldChunk != null) {
            --this.loadedChunkCount;
            this.unloadChunkSections(worldChunk);
            ClientChunkManager.this.world.unloadBlockEntities(worldChunk);
        }
        if (chunk != null) {
            ++this.loadedChunkCount;
            this.loadChunkSections(chunk);
        }
    }

    void unloadChunk(int index, WorldChunk chunk) {
        if (this.chunks.compareAndSet(index, chunk, null)) {
            --this.loadedChunkCount;
            this.unloadChunkSections(chunk);
        }
        ClientChunkManager.this.world.unloadBlockEntities(chunk);
    }

    public void onSectionStatusChanged(int x, int sectionY, int z, boolean previouslyEmpty) {
        if (!this.isInRadius(x, z)) {
            return;
        }
        long l = ChunkSectionPos.asLong(x, sectionY, z);
        if (previouslyEmpty) {
            this.activeSections.add(l);
        } else if (this.activeSections.remove(l)) {
            ClientChunkManager.this.world.onChunkUnload(l);
        }
    }

    private void unloadChunkSections(WorldChunk chunk) {
        ChunkSection[] chunkSections = chunk.getSectionArray();
        for (int i = 0; i < chunkSections.length; ++i) {
            ChunkPos chunkPos = chunk.getPos();
            this.activeSections.remove(ChunkSectionPos.asLong(chunkPos.x, chunk.sectionIndexToCoord(i), chunkPos.z));
        }
    }

    private void loadChunkSections(WorldChunk chunk) {
        ChunkSection[] chunkSections = chunk.getSectionArray();
        for (int i = 0; i < chunkSections.length; ++i) {
            ChunkSection chunkSection = chunkSections[i];
            if (!chunkSection.isEmpty()) continue;
            ChunkPos chunkPos = chunk.getPos();
            this.activeSections.add(ChunkSectionPos.asLong(chunkPos.x, chunk.sectionIndexToCoord(i), chunkPos.z));
        }
    }

    void refreshSections(WorldChunk chunk) {
        ChunkPos chunkPos = chunk.getPos();
        ChunkSection[] chunkSections = chunk.getSectionArray();
        for (int i = 0; i < chunkSections.length; ++i) {
            ChunkSection chunkSection = chunkSections[i];
            long l = ChunkSectionPos.asLong(chunkPos.x, chunk.sectionIndexToCoord(i), chunkPos.z);
            if (chunkSection.isEmpty()) {
                this.activeSections.add(l);
                continue;
            }
            if (!this.activeSections.remove(l)) continue;
            ClientChunkManager.this.world.onChunkUnload(l);
        }
    }

    boolean isInRadius(int chunkX, int chunkZ) {
        return Math.abs(chunkX - this.centerChunkX) <= this.radius && Math.abs(chunkZ - this.centerChunkZ) <= this.radius;
    }

    protected @Nullable WorldChunk getChunk(int index) {
        return this.chunks.get(index);
    }

    private void writePositions(String fileName) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);){
            int i = ClientChunkManager.this.chunks.radius;
            for (int j = this.centerChunkZ - i; j <= this.centerChunkZ + i; ++j) {
                for (int k = this.centerChunkX - i; k <= this.centerChunkX + i; ++k) {
                    WorldChunk worldChunk = ClientChunkManager.this.chunks.chunks.get(ClientChunkManager.this.chunks.getIndex(k, j));
                    if (worldChunk == null) continue;
                    ChunkPos chunkPos = worldChunk.getPos();
                    fileOutputStream.write((chunkPos.x + "\t" + chunkPos.z + "\t" + worldChunk.isEmpty() + "\n").getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to dump chunks to file {}", (Object)fileName, (Object)iOException);
        }
    }
}
