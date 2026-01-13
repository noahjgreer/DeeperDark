/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.VertexSorter
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.render.BlockRenderLayer
 *  net.minecraft.client.render.BlockRenderLayers
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.block.BlockModelRenderer
 *  net.minecraft.client.render.block.BlockRenderManager
 *  net.minecraft.client.render.block.entity.BlockEntityRenderManager
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.chunk.BlockBufferAllocatorStorage
 *  net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder
 *  net.minecraft.client.render.chunk.ChunkRendererRegion
 *  net.minecraft.client.render.chunk.SectionBuilder
 *  net.minecraft.client.render.chunk.SectionBuilder$RenderData
 *  net.minecraft.client.util.BufferAllocator
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockRenderView
 */
package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

@Environment(value=EnvType.CLIENT)
public class SectionBuilder {
    private final BlockRenderManager blockRenderManager;
    private final BlockEntityRenderManager blockEntityRenderDispatcher;

    public SectionBuilder(BlockRenderManager blockRenderManager, BlockEntityRenderManager blockEntityRenderDispatcher) {
        this.blockRenderManager = blockRenderManager;
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
    }

    public RenderData build(ChunkSectionPos sectionPos, ChunkRendererRegion renderRegion, VertexSorter vertexSorter, BlockBufferAllocatorStorage allocatorStorage) {
        RenderData renderData = new RenderData();
        BlockPos blockPos = sectionPos.getMinPos();
        BlockPos blockPos2 = blockPos.add(15, 15, 15);
        ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
        MatrixStack matrixStack = new MatrixStack();
        BlockModelRenderer.enableBrightnessCache();
        EnumMap map = new EnumMap(BlockRenderLayer.class);
        Random random = Random.create();
        ObjectArrayList list = new ObjectArrayList();
        for (BlockPos blockPos3 : BlockPos.iterate((BlockPos)blockPos, (BlockPos)blockPos2)) {
            BufferBuilder bufferBuilder;
            BlockRenderLayer blockRenderLayer;
            FluidState fluidState;
            BlockEntity blockEntity;
            BlockState blockState = renderRegion.getBlockState(blockPos3);
            if (blockState.isOpaqueFullCube()) {
                chunkOcclusionDataBuilder.markClosed(blockPos3);
            }
            if (blockState.hasBlockEntity() && (blockEntity = renderRegion.getBlockEntity(blockPos3)) != null) {
                this.addBlockEntity(renderData, blockEntity);
            }
            if (!(fluidState = blockState.getFluidState()).isEmpty()) {
                blockRenderLayer = BlockRenderLayers.getFluidLayer((FluidState)fluidState);
                bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, blockRenderLayer);
                this.blockRenderManager.renderFluid(blockPos3, (BlockRenderView)renderRegion, (VertexConsumer)bufferBuilder, blockState, fluidState);
            }
            if (blockState.getRenderType() != BlockRenderType.MODEL) continue;
            blockRenderLayer = BlockRenderLayers.getBlockLayer((BlockState)blockState);
            bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, blockRenderLayer);
            random.setSeed(blockState.getRenderingSeed(blockPos3));
            this.blockRenderManager.getModel(blockState).addParts(random, (List)list);
            matrixStack.push();
            matrixStack.translate((float)ChunkSectionPos.getLocalCoord((int)blockPos3.getX()), (float)ChunkSectionPos.getLocalCoord((int)blockPos3.getY()), (float)ChunkSectionPos.getLocalCoord((int)blockPos3.getZ()));
            this.blockRenderManager.renderBlock(blockState, blockPos3, (BlockRenderView)renderRegion, matrixStack, (VertexConsumer)bufferBuilder, true, (List)list);
            matrixStack.pop();
            list.clear();
        }
        for (Map.Entry entry : map.entrySet()) {
            BlockRenderLayer blockRenderLayer2 = (BlockRenderLayer)entry.getKey();
            BuiltBuffer builtBuffer = ((BufferBuilder)entry.getValue()).endNullable();
            if (builtBuffer == null) continue;
            if (blockRenderLayer2 == BlockRenderLayer.TRANSLUCENT) {
                renderData.translucencySortingData = builtBuffer.sortQuads(allocatorStorage.get(blockRenderLayer2), vertexSorter);
            }
            renderData.buffers.put(blockRenderLayer2, builtBuffer);
        }
        BlockModelRenderer.disableBrightnessCache();
        renderData.chunkOcclusionData = chunkOcclusionDataBuilder.build();
        return renderData;
    }

    private BufferBuilder beginBufferBuilding(Map<BlockRenderLayer, BufferBuilder> builders, BlockBufferAllocatorStorage allocatorStorage, BlockRenderLayer layer) {
        BufferBuilder bufferBuilder = builders.get(layer);
        if (bufferBuilder == null) {
            BufferAllocator bufferAllocator = allocatorStorage.get(layer);
            bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
            builders.put(layer, bufferBuilder);
        }
        return bufferBuilder;
    }

    private <E extends BlockEntity> void addBlockEntity(RenderData data, E blockEntity) {
        BlockEntityRenderer blockEntityRenderer = this.blockEntityRenderDispatcher.get(blockEntity);
        if (blockEntityRenderer != null && !blockEntityRenderer.rendersOutsideBoundingBox()) {
            data.blockEntities.add(blockEntity);
        }
    }
}

