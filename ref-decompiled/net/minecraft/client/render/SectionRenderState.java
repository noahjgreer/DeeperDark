/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderPass$RenderObject
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$ShapeIndexBuffer
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  com.mojang.blaze3d.vertex.VertexFormat$IndexType
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.GpuSampler
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.render.BlockRenderLayer
 *  net.minecraft.client.render.BlockRenderLayerGroup
 *  net.minecraft.client.render.SectionRenderState
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.SequencedCollection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayerGroup;

@Environment(value=EnvType.CLIENT)
public record SectionRenderState(GpuTextureView textureView, EnumMap<BlockRenderLayer, List<RenderPass.RenderObject<GpuBufferSlice[]>>> drawsPerLayer, int maxIndicesRequired, GpuBufferSlice[] chunkSectionInfos) {
    private final GpuTextureView textureView;
    private final EnumMap<BlockRenderLayer, List<RenderPass.RenderObject<GpuBufferSlice[]>>> drawsPerLayer;
    private final int maxIndicesRequired;
    private final GpuBufferSlice[] chunkSectionInfos;

    public SectionRenderState(GpuTextureView textureView, EnumMap<BlockRenderLayer, List<RenderPass.RenderObject<GpuBufferSlice[]>>> drawsPerLayer, int maxIndicesRequired, GpuBufferSlice[] chunkSectionInfos) {
        this.textureView = textureView;
        this.drawsPerLayer = drawsPerLayer;
        this.maxIndicesRequired = maxIndicesRequired;
        this.chunkSectionInfos = chunkSectionInfos;
    }

    public void renderSection(BlockRenderLayerGroup group, GpuSampler sampler) {
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer((VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = this.maxIndicesRequired == 0 ? null : shapeIndexBuffer.getIndexBuffer(this.maxIndicesRequired);
        VertexFormat.IndexType indexType = this.maxIndicesRequired == 0 ? null : shapeIndexBuffer.getIndexType();
        BlockRenderLayer[] blockRenderLayers = group.getLayers();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl = SharedConstants.HOTKEYS && minecraftClient.wireFrame;
        Framebuffer framebuffer = group.getFramebuffer();
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Section layers for " + group.getName(), framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty());){
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.bindTexture("Sampler2", minecraftClient.gameRenderer.getLightmapTextureManager().getGlTextureView(), RenderSystem.getSamplerCache().get(FilterMode.LINEAR));
            for (BlockRenderLayer blockRenderLayer : blockRenderLayers) {
                SequencedCollection list = (List)this.drawsPerLayer.get(blockRenderLayer);
                if (list.isEmpty()) continue;
                if (blockRenderLayer == BlockRenderLayer.TRANSLUCENT) {
                    list = list.reversed();
                }
                renderPass.setPipeline(bl ? RenderPipelines.WIREFRAME : blockRenderLayer.getPipeline());
                renderPass.bindTexture("Sampler0", this.textureView, sampler);
                renderPass.drawMultipleIndexed((Collection)list, gpuBuffer, indexType, List.of("ChunkSection"), (Object)this.chunkSectionInfos);
            }
        }
    }

    public GpuTextureView textureView() {
        return this.textureView;
    }

    public EnumMap<BlockRenderLayer, List<RenderPass.RenderObject<GpuBufferSlice[]>>> drawsPerLayer() {
        return this.drawsPerLayer;
    }

    public int maxIndicesRequired() {
        return this.maxIndicesRequired;
    }

    public GpuBufferSlice[] chunkSectionInfos() {
        return this.chunkSectionInfos;
    }
}

