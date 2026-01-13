/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.ChunkRenderingDataPreparer;

@Environment(value=EnvType.CLIENT)
static final class ChunkRenderingDataPreparer.PreparerState
extends Record {
    final ChunkRenderingDataPreparer.RenderableChunks storage;
    final ChunkRenderingDataPreparer.Events events;

    ChunkRenderingDataPreparer.PreparerState(BuiltChunkStorage storage) {
        this(new ChunkRenderingDataPreparer.RenderableChunks(storage), new ChunkRenderingDataPreparer.Events());
    }

    private ChunkRenderingDataPreparer.PreparerState(ChunkRenderingDataPreparer.RenderableChunks storage, ChunkRenderingDataPreparer.Events events) {
        this.storage = storage;
        this.events = events;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChunkRenderingDataPreparer.PreparerState.class, "storage;events", "storage", "events"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChunkRenderingDataPreparer.PreparerState.class, "storage;events", "storage", "events"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChunkRenderingDataPreparer.PreparerState.class, "storage;events", "storage", "events"}, this, object);
    }

    public ChunkRenderingDataPreparer.RenderableChunks storage() {
        return this.storage;
    }

    public ChunkRenderingDataPreparer.Events events() {
        return this.events;
    }
}
