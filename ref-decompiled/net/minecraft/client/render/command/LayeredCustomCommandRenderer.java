/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.FilterMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.particle.BillboardParticleSubmittable$Buffers
 *  net.minecraft.client.render.command.BatchingRenderCommandQueue
 *  net.minecraft.client.render.command.LayeredCustomCommandRenderer
 *  net.minecraft.client.render.command.LayeredCustomCommandRenderer$VerticesCache
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue$LayeredCustom
 *  net.minecraft.client.texture.TextureManager
 */
package net.minecraft.client.render.command;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
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
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.LayeredCustomCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.TextureManager;

@Environment(value=EnvType.CLIENT)
public class LayeredCustomCommandRenderer
implements AutoCloseable {
    private final Queue<VerticesCache> availableBuffers = new ArrayDeque();
    private final List<VerticesCache> usedBuffers = new ArrayList();

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
            VerticesCache verticesCache = (VerticesCache)this.availableBuffers.poll();
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
}

