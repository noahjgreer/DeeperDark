/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.render.BlockRenderLayer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.chunk.AbstractChunkRenderData
 *  net.minecraft.client.render.chunk.Buffers
 *  net.minecraft.client.render.chunk.ChunkOcclusionData
 *  net.minecraft.client.render.chunk.ChunkRenderData
 *  net.minecraft.client.render.chunk.NormalizedRelativePos
 *  net.minecraft.client.render.chunk.SectionBuilder$RenderData
 *  net.minecraft.client.util.BufferAllocator$CloseableBuffer
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.chunk.AbstractChunkRenderData;
import net.minecraft.client.render.chunk.Buffers;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.client.render.chunk.NormalizedRelativePos;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkRenderData
implements AbstractChunkRenderData {
    public static final AbstractChunkRenderData HIDDEN = new /* Unavailable Anonymous Inner Class!! */;
    public static final AbstractChunkRenderData READY = new /* Unavailable Anonymous Inner Class!! */;
    private final List<BlockEntity> blockEntities;
    private final ChunkOcclusionData chunkOcclusionData;
    private final // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BuiltBuffer.SortState translucencySortingData;
    private @Nullable NormalizedRelativePos pos;
    private final Map<BlockRenderLayer, Buffers> buffersByLayer = new EnumMap(BlockRenderLayer.class);

    public ChunkRenderData(NormalizedRelativePos pos, SectionBuilder.RenderData renderData) {
        this.pos = pos;
        this.chunkOcclusionData = renderData.chunkOcclusionData;
        this.blockEntities = renderData.blockEntities;
        this.translucencySortingData = renderData.translucencySortingData;
    }

    public void setPos(NormalizedRelativePos pos) {
        this.pos = pos;
    }

    public boolean hasPosition(NormalizedRelativePos pos) {
        return !pos.equals((Object)this.pos);
    }

    public boolean hasData() {
        return !this.buffersByLayer.isEmpty();
    }

    public boolean containsLayer(BlockRenderLayer layer) {
        return !this.buffersByLayer.containsKey(layer);
    }

    public List<BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public boolean isVisibleThrough(Direction from, Direction to) {
        return this.chunkOcclusionData.isVisibleThrough(from, to);
    }

    public @Nullable Buffers getBuffersForLayer(BlockRenderLayer layer) {
        return (Buffers)this.buffersByLayer.get(layer);
    }

    public void upload(BlockRenderLayer layer, BuiltBuffer builtBuffer, long sectionPos) {
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        Buffers buffers = this.getBuffersForLayer(layer);
        if (buffers != null) {
            if (buffers.getVertexBuffer().size() < (long)builtBuffer.getBuffer().remaining()) {
                buffers.getVertexBuffer().close();
                buffers.setVertexBuffer(RenderSystem.getDevice().createBuffer(() -> "Section vertex buffer - layer: " + layer.getName() + "; cords: " + ChunkSectionPos.unpackX((long)sectionPos) + ", " + ChunkSectionPos.unpackY((long)sectionPos) + ", " + ChunkSectionPos.unpackZ((long)sectionPos), 40, builtBuffer.getBuffer()));
            } else if (!buffers.getVertexBuffer().isClosed()) {
                commandEncoder.writeToBuffer(buffers.getVertexBuffer().slice(), builtBuffer.getBuffer());
            }
            ByteBuffer byteBuffer = builtBuffer.getSortedBuffer();
            if (byteBuffer != null) {
                if (buffers.getIndexBuffer() == null || buffers.getIndexBuffer().size() < (long)byteBuffer.remaining()) {
                    if (buffers.getIndexBuffer() != null) {
                        buffers.getIndexBuffer().close();
                    }
                    buffers.setIndexBuffer(RenderSystem.getDevice().createBuffer(() -> "Section index buffer - layer: " + layer.getName() + "; cords: " + ChunkSectionPos.unpackX((long)sectionPos) + ", " + ChunkSectionPos.unpackY((long)sectionPos) + ", " + ChunkSectionPos.unpackZ((long)sectionPos), 72, byteBuffer));
                } else if (!buffers.getIndexBuffer().isClosed()) {
                    commandEncoder.writeToBuffer(buffers.getIndexBuffer().slice(), byteBuffer);
                }
            } else if (buffers.getIndexBuffer() != null) {
                buffers.getIndexBuffer().close();
                buffers.setIndexBuffer(null);
            }
            buffers.setIndexCount(builtBuffer.getDrawParameters().indexCount());
            buffers.setIndexType(builtBuffer.getDrawParameters().indexType());
        } else {
            GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Section vertex buffer - layer: " + layer.getName() + "; cords: " + ChunkSectionPos.unpackX((long)sectionPos) + ", " + ChunkSectionPos.unpackY((long)sectionPos) + ", " + ChunkSectionPos.unpackZ((long)sectionPos), 40, builtBuffer.getBuffer());
            ByteBuffer byteBuffer2 = builtBuffer.getSortedBuffer();
            GpuBuffer gpuBuffer2 = byteBuffer2 != null ? RenderSystem.getDevice().createBuffer(() -> "Section index buffer - layer: " + layer.getName() + "; cords: " + ChunkSectionPos.unpackX((long)sectionPos) + ", " + ChunkSectionPos.unpackY((long)sectionPos) + ", " + ChunkSectionPos.unpackZ((long)sectionPos), 72, byteBuffer2) : null;
            Buffers buffers2 = new Buffers(gpuBuffer, gpuBuffer2, builtBuffer.getDrawParameters().indexCount(), builtBuffer.getDrawParameters().indexType());
            this.buffersByLayer.put(layer, buffers2);
        }
    }

    public void uploadIndexBuffer(BlockRenderLayer layer, BufferAllocator.CloseableBuffer buffer, long sectionPos) {
        Buffers buffers = this.getBuffersForLayer(layer);
        if (buffers == null) {
            return;
        }
        if (buffers.getIndexBuffer() == null) {
            buffers.setIndexBuffer(RenderSystem.getDevice().createBuffer(() -> "Section index buffer - layer: " + layer.getName() + "; cords: " + ChunkSectionPos.unpackX((long)sectionPos) + ", " + ChunkSectionPos.unpackY((long)sectionPos) + ", " + ChunkSectionPos.unpackZ((long)sectionPos), 72, buffer.getBuffer()));
        } else {
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            if (!buffers.getIndexBuffer().isClosed()) {
                commandEncoder.writeToBuffer(buffers.getIndexBuffer().slice(), buffer.getBuffer());
            }
        }
    }

    public boolean hasTranslucentLayers() {
        return this.buffersByLayer.containsKey(BlockRenderLayer.TRANSLUCENT);
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BuiltBuffer.SortState getTranslucencySortingData() {
        return this.translucencySortingData;
    }

    public void close() {
        this.buffersByLayer.values().forEach(Buffers::close);
        this.buffersByLayer.clear();
    }
}

