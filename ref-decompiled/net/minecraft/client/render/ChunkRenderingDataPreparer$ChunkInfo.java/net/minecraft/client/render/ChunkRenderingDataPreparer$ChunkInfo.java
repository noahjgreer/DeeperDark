/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
@Debug
public static class ChunkRenderingDataPreparer.ChunkInfo {
    @Debug
    protected final ChunkBuilder.BuiltChunk chunk;
    private byte direction;
    byte cullingState;
    @Debug
    public final int propagationLevel;

    ChunkRenderingDataPreparer.ChunkInfo(ChunkBuilder.BuiltChunk chunk, @Nullable Direction direction, int propagationLevel) {
        this.chunk = chunk;
        if (direction != null) {
            this.addDirection(direction);
        }
        this.propagationLevel = propagationLevel;
    }

    void updateCullingState(byte parentCullingState, Direction from) {
        this.cullingState = (byte)(this.cullingState | (parentCullingState | 1 << from.ordinal()));
    }

    boolean canCull(Direction from) {
        return (this.cullingState & 1 << from.ordinal()) > 0;
    }

    void addDirection(Direction direction) {
        this.direction = (byte)(this.direction | (this.direction | 1 << direction.ordinal()));
    }

    @Debug
    public boolean hasDirection(int ordinal) {
        return (this.direction & 1 << ordinal) > 0;
    }

    boolean hasAnyDirection() {
        return this.direction != 0;
    }

    public int hashCode() {
        return Long.hashCode(this.chunk.getSectionPos());
    }

    public boolean equals(Object o) {
        if (!(o instanceof ChunkRenderingDataPreparer.ChunkInfo)) {
            return false;
        }
        ChunkRenderingDataPreparer.ChunkInfo chunkInfo = (ChunkRenderingDataPreparer.ChunkInfo)o;
        return this.chunk.getSectionPos() == chunkInfo.chunk.getSectionPos();
    }
}
