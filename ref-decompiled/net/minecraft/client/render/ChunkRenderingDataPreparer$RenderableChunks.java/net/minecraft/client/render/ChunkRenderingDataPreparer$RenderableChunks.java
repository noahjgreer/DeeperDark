/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;

@Environment(value=EnvType.CLIENT)
static class ChunkRenderingDataPreparer.RenderableChunks {
    public final ChunkRenderingDataPreparer.ChunkInfoList infoList;
    public final Octree octree;
    public final Long2ObjectMap<List<ChunkBuilder.BuiltChunk>> queue;

    public ChunkRenderingDataPreparer.RenderableChunks(BuiltChunkStorage storage) {
        this.infoList = new ChunkRenderingDataPreparer.ChunkInfoList(storage.chunks.length);
        this.octree = new Octree(storage.getSectionPos(), storage.getViewDistance(), storage.sizeY, storage.world.getBottomY());
        this.queue = new Long2ObjectOpenHashMap();
    }
}
