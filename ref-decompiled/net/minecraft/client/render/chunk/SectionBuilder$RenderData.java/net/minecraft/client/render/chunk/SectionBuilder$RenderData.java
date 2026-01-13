/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static final class SectionBuilder.RenderData {
    public final List<BlockEntity> blockEntities = new ArrayList<BlockEntity>();
    public final Map<BlockRenderLayer, BuiltBuffer> buffers = new EnumMap<BlockRenderLayer, BuiltBuffer>(BlockRenderLayer.class);
    public ChunkOcclusionData chunkOcclusionData = new ChunkOcclusionData();
    public @Nullable BuiltBuffer.SortState translucencySortingData;

    public void close() {
        this.buffers.values().forEach(BuiltBuffer::close);
    }
}
