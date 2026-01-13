/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.AbstractChunkRenderData;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
class ChunkRenderData.1
implements AbstractChunkRenderData {
    ChunkRenderData.1() {
    }

    @Override
    public boolean isVisibleThrough(Direction from, Direction to) {
        return false;
    }
}
