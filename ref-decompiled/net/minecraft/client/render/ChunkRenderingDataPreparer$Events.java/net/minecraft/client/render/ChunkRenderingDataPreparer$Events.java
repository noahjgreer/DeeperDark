/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;

@Environment(value=EnvType.CLIENT)
static final class ChunkRenderingDataPreparer.Events
extends Record {
    final LongSet chunksWhichReceivedNeighbors;
    final BlockingQueue<ChunkBuilder.BuiltChunk> sectionsToPropagateFrom;

    ChunkRenderingDataPreparer.Events() {
        this((LongSet)new LongOpenHashSet(), new LinkedBlockingQueue<ChunkBuilder.BuiltChunk>());
    }

    private ChunkRenderingDataPreparer.Events(LongSet chunksWhichReceivedNeighbors, BlockingQueue<ChunkBuilder.BuiltChunk> sectionsToPropagateFrom) {
        this.chunksWhichReceivedNeighbors = chunksWhichReceivedNeighbors;
        this.sectionsToPropagateFrom = sectionsToPropagateFrom;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChunkRenderingDataPreparer.Events.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChunkRenderingDataPreparer.Events.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChunkRenderingDataPreparer.Events.class, "chunksWhichReceivedNeighbors;sectionsToPropagateFrom", "chunksWhichReceivedNeighbors", "sectionsToPropagateFrom"}, this, object);
    }

    public LongSet chunksWhichReceivedNeighbors() {
        return this.chunksWhichReceivedNeighbors;
    }

    public BlockingQueue<ChunkBuilder.BuiltChunk> sectionsToPropagateFrom() {
        return this.sectionsToPropagateFrom;
    }
}
