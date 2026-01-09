package net.minecraft.client.render.chunk;

import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SectionBuilder {
   private final BlockRenderManager blockRenderManager;
   private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

   public SectionBuilder(BlockRenderManager blockRenderManager, BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
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
      Map map = new EnumMap(BlockRenderLayer.class);
      Random random = Random.create();
      List list = new ObjectArrayList();
      Iterator var13 = BlockPos.iterate(blockPos, blockPos2).iterator();

      while(var13.hasNext()) {
         BlockPos blockPos3 = (BlockPos)var13.next();
         BlockState blockState = renderRegion.getBlockState(blockPos3);
         if (blockState.isOpaqueFullCube()) {
            chunkOcclusionDataBuilder.markClosed(blockPos3);
         }

         if (blockState.hasBlockEntity()) {
            BlockEntity blockEntity = renderRegion.getBlockEntity(blockPos3);
            if (blockEntity != null) {
               this.addBlockEntity(renderData, blockEntity);
            }
         }

         FluidState fluidState = blockState.getFluidState();
         BlockRenderLayer blockRenderLayer;
         BufferBuilder bufferBuilder;
         if (!fluidState.isEmpty()) {
            blockRenderLayer = RenderLayers.getFluidLayer(fluidState);
            bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, blockRenderLayer);
            this.blockRenderManager.renderFluid(blockPos3, renderRegion, bufferBuilder, blockState, fluidState);
         }

         if (blockState.getRenderType() == BlockRenderType.MODEL) {
            blockRenderLayer = RenderLayers.getBlockLayer(blockState);
            bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, blockRenderLayer);
            random.setSeed(blockState.getRenderingSeed(blockPos3));
            this.blockRenderManager.getModel(blockState).addParts(random, list);
            matrixStack.push();
            matrixStack.translate((float)ChunkSectionPos.getLocalCoord(blockPos3.getX()), (float)ChunkSectionPos.getLocalCoord(blockPos3.getY()), (float)ChunkSectionPos.getLocalCoord(blockPos3.getZ()));
            this.blockRenderManager.renderBlock(blockState, blockPos3, renderRegion, matrixStack, bufferBuilder, true, list);
            matrixStack.pop();
            list.clear();
         }
      }

      var13 = map.entrySet().iterator();

      while(var13.hasNext()) {
         Map.Entry entry = (Map.Entry)var13.next();
         BlockRenderLayer blockRenderLayer2 = (BlockRenderLayer)entry.getKey();
         BuiltBuffer builtBuffer = ((BufferBuilder)entry.getValue()).endNullable();
         if (builtBuffer != null) {
            if (blockRenderLayer2 == BlockRenderLayer.TRANSLUCENT) {
               renderData.translucencySortingData = builtBuffer.sortQuads(allocatorStorage.get(blockRenderLayer2), vertexSorter);
            }

            renderData.buffers.put(blockRenderLayer2, builtBuffer);
         }
      }

      BlockModelRenderer.disableBrightnessCache();
      renderData.chunkOcclusionData = chunkOcclusionDataBuilder.build();
      return renderData;
   }

   private BufferBuilder beginBufferBuilding(Map builders, BlockBufferAllocatorStorage allocatorStorage, BlockRenderLayer layer) {
      BufferBuilder bufferBuilder = (BufferBuilder)builders.get(layer);
      if (bufferBuilder == null) {
         BufferAllocator bufferAllocator = allocatorStorage.get(layer);
         bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
         builders.put(layer, bufferBuilder);
      }

      return bufferBuilder;
   }

   private void addBlockEntity(RenderData data, BlockEntity blockEntity) {
      BlockEntityRenderer blockEntityRenderer = this.blockEntityRenderDispatcher.get(blockEntity);
      if (blockEntityRenderer != null && !blockEntityRenderer.rendersOutsideBoundingBox()) {
         data.blockEntities.add(blockEntity);
      }

   }

   @Environment(EnvType.CLIENT)
   public static final class RenderData {
      public final List blockEntities = new ArrayList();
      public final Map buffers = new EnumMap(BlockRenderLayer.class);
      public ChunkOcclusionData chunkOcclusionData = new ChunkOcclusionData();
      @Nullable
      public BuiltBuffer.SortState translucencySortingData;

      public void close() {
         this.buffers.values().forEach(BuiltBuffer::close);
      }
   }
}
