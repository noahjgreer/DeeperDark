/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.BuiltChunkStorage
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.chunk.ChunkBuilder
 *  net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.world.HeightLimitView
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BuiltChunkStorage {
    protected final WorldRenderer worldRenderer;
    protected final World world;
    protected int sizeY;
    protected int sizeX;
    protected int sizeZ;
    private int viewDistance;
    private ChunkSectionPos sectionPos;
    public ChunkBuilder.BuiltChunk[] chunks;

    public BuiltChunkStorage(ChunkBuilder chunkBuilder, World world, int viewDistance, WorldRenderer worldRenderer) {
        this.worldRenderer = worldRenderer;
        this.world = world;
        this.setViewDistance(viewDistance);
        this.createChunks(chunkBuilder);
        this.sectionPos = ChunkSectionPos.from((int)(this.viewDistance + 1), (int)0, (int)(this.viewDistance + 1));
    }

    protected void createChunks(ChunkBuilder chunkBuilder) {
        if (!MinecraftClient.getInstance().isOnThread()) {
            throw new IllegalStateException("createSections called from wrong thread: " + Thread.currentThread().getName());
        }
        int i = this.sizeX * this.sizeY * this.sizeZ;
        this.chunks = new ChunkBuilder.BuiltChunk[i];
        for (int j = 0; j < this.sizeX; ++j) {
            for (int k = 0; k < this.sizeY; ++k) {
                for (int l = 0; l < this.sizeZ; ++l) {
                    int m = this.getChunkIndex(j, k, l);
                    ChunkBuilder chunkBuilder2 = chunkBuilder;
                    Objects.requireNonNull(chunkBuilder2);
                    this.chunks[m] = new ChunkBuilder.BuiltChunk(chunkBuilder2, m, ChunkSectionPos.asLong((int)j, (int)(k + this.world.getBottomSectionCoord()), (int)l));
                }
            }
        }
    }

    public void clear() {
        for (ChunkBuilder.BuiltChunk builtChunk : this.chunks) {
            builtChunk.clear();
        }
    }

    private int getChunkIndex(int x, int y, int z) {
        return (z * this.sizeY + y) * this.sizeX + x;
    }

    protected void setViewDistance(int viewDistance) {
        int i;
        this.sizeX = i = viewDistance * 2 + 1;
        this.sizeY = this.world.countVerticalSections();
        this.sizeZ = i;
        this.viewDistance = viewDistance;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public HeightLimitView getWorld() {
        return this.world;
    }

    public void updateCameraPosition(ChunkSectionPos sectionPos) {
        for (int i = 0; i < this.sizeX; ++i) {
            int j = sectionPos.getSectionX() - this.viewDistance;
            int k = j + Math.floorMod(i - j, this.sizeX);
            for (int l = 0; l < this.sizeZ; ++l) {
                int m = sectionPos.getSectionZ() - this.viewDistance;
                int n = m + Math.floorMod(l - m, this.sizeZ);
                for (int o = 0; o < this.sizeY; ++o) {
                    int p = this.world.getBottomSectionCoord() + o;
                    ChunkBuilder.BuiltChunk builtChunk = this.chunks[this.getChunkIndex(i, o, l)];
                    long q = builtChunk.getSectionPos();
                    if (q == ChunkSectionPos.asLong((int)k, (int)p, (int)n)) continue;
                    builtChunk.setSectionPos(ChunkSectionPos.asLong((int)k, (int)p, (int)n));
                }
            }
        }
        this.sectionPos = sectionPos;
        this.worldRenderer.getChunkRenderingDataPreparer().scheduleTerrainUpdate();
    }

    public ChunkSectionPos getSectionPos() {
        return this.sectionPos;
    }

    public void scheduleRebuild(int x, int y, int z, boolean important) {
        ChunkBuilder.BuiltChunk builtChunk = this.getRenderedChunk(x, y, z);
        if (builtChunk != null) {
            builtChunk.scheduleRebuild(important);
        }
    }

    protected // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkBuilder.BuiltChunk getRenderedChunk(BlockPos blockPos) {
        return this.getRenderedChunk(ChunkSectionPos.toLong((BlockPos)blockPos));
    }

    protected // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkBuilder.BuiltChunk getRenderedChunk(long sectionPos) {
        int i = ChunkSectionPos.unpackX((long)sectionPos);
        int j = ChunkSectionPos.unpackY((long)sectionPos);
        int k = ChunkSectionPos.unpackZ((long)sectionPos);
        return this.getRenderedChunk(i, j, k);
    }

    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkBuilder.BuiltChunk getRenderedChunk(int sectionX, int sectionY, int sectionZ) {
        if (!this.isSectionWithinViewDistance(sectionX, sectionY, sectionZ)) {
            return null;
        }
        int i = sectionY - this.world.getBottomSectionCoord();
        int j = Math.floorMod(sectionX, this.sizeX);
        int k = Math.floorMod(sectionZ, this.sizeZ);
        return this.chunks[this.getChunkIndex(j, i, k)];
    }

    private boolean isSectionWithinViewDistance(int sectionX, int sectionY, int sectionZ) {
        if (sectionY < this.world.getBottomSectionCoord() || sectionY > this.world.getTopSectionCoord()) {
            return false;
        }
        if (sectionX < this.sectionPos.getSectionX() - this.viewDistance || sectionX > this.sectionPos.getSectionX() + this.viewDistance) {
            return false;
        }
        return sectionZ >= this.sectionPos.getSectionZ() - this.viewDistance && sectionZ <= this.sectionPos.getSectionZ() + this.viewDistance;
    }
}

