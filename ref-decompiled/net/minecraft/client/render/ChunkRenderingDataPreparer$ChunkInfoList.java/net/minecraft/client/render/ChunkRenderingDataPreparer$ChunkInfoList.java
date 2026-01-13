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
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class ChunkRenderingDataPreparer.ChunkInfoList {
    private final ChunkRenderingDataPreparer.ChunkInfo[] current;

    ChunkRenderingDataPreparer.ChunkInfoList(int size) {
        this.current = new ChunkRenderingDataPreparer.ChunkInfo[size];
    }

    public void setInfo(ChunkBuilder.BuiltChunk chunk, ChunkRenderingDataPreparer.ChunkInfo info) {
        this.current[chunk.index] = info;
    }

    public  @Nullable ChunkRenderingDataPreparer.ChunkInfo getInfo(ChunkBuilder.BuiltChunk chunk) {
        int i = chunk.index;
        if (i < 0 || i >= this.current.length) {
            return null;
        }
        return this.current[i];
    }
}
