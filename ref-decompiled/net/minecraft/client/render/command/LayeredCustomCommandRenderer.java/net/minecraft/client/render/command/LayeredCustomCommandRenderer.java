/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.command;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Queue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.TextureManager;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LayeredCustomCommandRenderer
implements AutoCloseable {
    private final Queue<VerticesCache> availableBuffers = new ArrayDeque<VerticesCache>();
    private final List<VerticesCache> usedBuffers = new ArrayList<VerticesCache>();

    public void render(BatchingRenderCommandQueue queue) {
        if (queue.getLayeredCustomCommands().isEmpty()) {
            return;
        }
        GpuDevice gpuDevice = RenderSystem.getDevice();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextureManager textureManager = minecraftClient.getTextureManager();
        Framebuffer framebuffer = minecraftClient.getFramebuffer();
        Framebuffer framebuffer2 = minecraftClient.worldRenderer.getParticlesFramebuffer();
        for (OrderedRenderCommandQueue.LayeredCustom layeredCustom : queue.getLayeredCustomCommands()) {
            VerticesCache verticesCache = this.availableBuffers.poll();
            if (verticesCache == null) {
                verticesCache = new VerticesCache();
            }
            this.usedBuffers.add(verticesCache);
            BillboardParticleSubmittable.Buffers buffers = layeredCustom.submit(verticesCache);
            if (buffers == null) continue;
            try (RenderPass renderPass = gpuDevice.createCommandEncoder().createRenderPass(() -> "Particles - Main", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty());){
                this.submitRenderPass(renderPass);
                layeredCustom.render(buffers, verticesCache, renderPass, textureManager, false);
                if (framebuffer2 == null) {
                    layeredCustom.render(buffers, verticesCache, renderPass, textureManager, true);
                }
            }
            if (framebuffer2 == null) continue;
            renderPass = gpuDevice.createCommandEncoder().createRenderPass(() -> "Particles - Transparent", framebuffer2.getColorAttachmentView(), OptionalInt.empty(), framebuffer2.getDepthAttachmentView(), OptionalDouble.empty());
            try {
                this.submitRenderPass(renderPass);
                layeredCustom.render(buffers, verticesCache, renderPass, textureManager, true);
            }
            finally {
                if (renderPass == null) continue;
                renderPass.close();
            }
        }
    }

    public void end() {
        for (VerticesCache verticesCache : this.usedBuffers) {
            verticesCache.rotate();
        }
        this.availableBuffers.addAll(this.usedBuffers);
        this.usedBuffers.clear();
    }

    private void submitRenderPass(RenderPass renderPass) {
        renderPass.setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
        renderPass.setUniform("Fog", RenderSystem.getShaderFog());
        renderPass.bindTexture("Sampler2", MinecraftClient.getInstance().gameRenderer.getLightmapTextureManager().getGlTextureView(), RenderSystem.getSamplerCache().get(FilterMode.LINEAR));
    }

    @Override
    public void close() {
        this.availableBuffers.forEach(VerticesCache::close);
    }

    @Environment(value=EnvType.CLIENT)
    public static class VerticesCache
    implements AutoCloseable {
        private @Nullable MappableRingBuffer ringBuffer;

        public void write(ByteBuffer byteBuffer) {
            if (this.ringBuffer == null || this.ringBuffer.size() < byteBuffer.remaining()) {
                if (this.ringBuffer != null) {
                    this.ringBuffer.close();
                }
                this.ringBuffer = new MappableRingBuffer(() -> "Particle Vertices", 34, byteBuffer.remaining());
            }
            try (GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.ringBuffer.getBlocking().slice(), false, true);){
                mappedView.data().put(byteBuffer);
            }
        }

        public GpuBuffer get() {
            if (this.ringBuffer == null) {
                throw new IllegalStateException("Can't get buffer before it's made");
            }
            return this.ringBuffer.getBlocking();
        }

        void rotate() {
            if (this.ringBuffer != null) {
                this.ringBuffer.rotate();
            }
        }

        @Override
        public void close() {
            if (this.ringBuffer != null) {
                this.ringBuffer.close();
            }
        }
    }
}
