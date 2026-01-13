/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderPass$RenderObject
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$ShapeIndexBuffer
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.WorldBorderRendering
 *  net.minecraft.client.render.state.WorldBorderRenderState
 *  net.minecraft.client.render.state.WorldBorderRenderState$Distance
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.BufferAllocator
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.border.WorldBorder
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.state.WorldBorderRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class WorldBorderRendering {
    public static final Identifier FORCEFIELD = Identifier.ofVanilla((String)"textures/misc/forcefield.png");
    private boolean forceRefreshBuffers = true;
    private double lastUploadedBoundWest;
    private double lastUploadedBoundNorth;
    private double lastXMin;
    private double lastXMax;
    private double lastZMin;
    private double lastZMax;
    private final GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "World border vertex buffer", 40, 16L * (long)VertexFormats.POSITION_TEXTURE.getVertexSize());
    private final RenderSystem.ShapeIndexBuffer indexBuffer = RenderSystem.getSequentialBuffer((VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS);

    private void refreshDirectionBuffer(WorldBorderRenderState state, double viewDistanceBlocks, double z, double x, float farPlaneDistance, float vMin, float vMax) {
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized((int)(VertexFormats.POSITION_TEXTURE.getVertexSize() * 4 * 4));){
            double d = state.minX;
            double e = state.maxX;
            double f = state.minZ;
            double g = state.maxZ;
            double h = Math.max((double)MathHelper.floor((double)(z - viewDistanceBlocks)), f);
            double i = Math.min((double)MathHelper.ceil((double)(z + viewDistanceBlocks)), g);
            float j = (float)(MathHelper.floor((double)h) & 1) * 0.5f;
            float k = (float)(i - h) / 2.0f;
            double l = Math.max((double)MathHelper.floor((double)(x - viewDistanceBlocks)), d);
            double m = Math.min((double)MathHelper.ceil((double)(x + viewDistanceBlocks)), e);
            float n = (float)(MathHelper.floor((double)l) & 1) * 0.5f;
            float o = (float)(m - l) / 2.0f;
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            bufferBuilder.vertex(0.0f, -farPlaneDistance, (float)(g - h)).texture(n, vMin);
            bufferBuilder.vertex((float)(m - l), -farPlaneDistance, (float)(g - h)).texture(o + n, vMin);
            bufferBuilder.vertex((float)(m - l), farPlaneDistance, (float)(g - h)).texture(o + n, vMax);
            bufferBuilder.vertex(0.0f, farPlaneDistance, (float)(g - h)).texture(n, vMax);
            bufferBuilder.vertex(0.0f, -farPlaneDistance, 0.0f).texture(j, vMin);
            bufferBuilder.vertex(0.0f, -farPlaneDistance, (float)(i - h)).texture(k + j, vMin);
            bufferBuilder.vertex(0.0f, farPlaneDistance, (float)(i - h)).texture(k + j, vMax);
            bufferBuilder.vertex(0.0f, farPlaneDistance, 0.0f).texture(j, vMax);
            bufferBuilder.vertex((float)(m - l), -farPlaneDistance, 0.0f).texture(n, vMin);
            bufferBuilder.vertex(0.0f, -farPlaneDistance, 0.0f).texture(o + n, vMin);
            bufferBuilder.vertex(0.0f, farPlaneDistance, 0.0f).texture(o + n, vMax);
            bufferBuilder.vertex((float)(m - l), farPlaneDistance, 0.0f).texture(n, vMax);
            bufferBuilder.vertex((float)(e - l), -farPlaneDistance, (float)(i - h)).texture(j, vMin);
            bufferBuilder.vertex((float)(e - l), -farPlaneDistance, 0.0f).texture(k + j, vMin);
            bufferBuilder.vertex((float)(e - l), farPlaneDistance, 0.0f).texture(k + j, vMax);
            bufferBuilder.vertex((float)(e - l), farPlaneDistance, (float)(i - h)).texture(j, vMax);
            try (BuiltBuffer builtBuffer = bufferBuilder.end();){
                RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.vertexBuffer.slice(), builtBuffer.getBuffer());
            }
            this.lastXMin = d;
            this.lastXMax = e;
            this.lastZMin = f;
            this.lastZMax = g;
            this.lastUploadedBoundWest = l;
            this.lastUploadedBoundNorth = h;
            this.forceRefreshBuffers = false;
        }
    }

    public void updateRenderState(WorldBorder border, float tickProgress, Vec3d cameraPos, double viewDistanceBlocks, WorldBorderRenderState state) {
        state.minX = border.getBoundWest(tickProgress);
        state.maxX = border.getBoundEast(tickProgress);
        state.minZ = border.getBoundNorth(tickProgress);
        state.maxZ = border.getBoundSouth(tickProgress);
        if (cameraPos.x < state.maxX - viewDistanceBlocks && cameraPos.x > state.minX + viewDistanceBlocks && cameraPos.z < state.maxZ - viewDistanceBlocks && cameraPos.z > state.minZ + viewDistanceBlocks || cameraPos.x < state.minX - viewDistanceBlocks || cameraPos.x > state.maxX + viewDistanceBlocks || cameraPos.z < state.minZ - viewDistanceBlocks || cameraPos.z > state.maxZ + viewDistanceBlocks) {
            state.alpha = 0.0;
            return;
        }
        state.alpha = 1.0 - border.getDistanceInsideBorder(cameraPos.x, cameraPos.z) / viewDistanceBlocks;
        state.alpha = Math.pow(state.alpha, 4.0);
        state.alpha = MathHelper.clamp((double)state.alpha, (double)0.0, (double)1.0);
        state.tint = border.getStage().getColor();
    }

    public void render(WorldBorderRenderState state, Vec3d cameraPos, double viewDistanceBlocks, double farPlaneDistance) {
        GpuTextureView gpuTextureView2;
        GpuTextureView gpuTextureView;
        if (state.alpha <= 0.0) {
            return;
        }
        double d = cameraPos.x;
        double e = cameraPos.z;
        float f = (float)farPlaneDistance;
        float g = (float)ColorHelper.getRed((int)state.tint) / 255.0f;
        float h = (float)ColorHelper.getGreen((int)state.tint) / 255.0f;
        float i = (float)ColorHelper.getBlue((int)state.tint) / 255.0f;
        float j = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0f;
        float k = (float)(-MathHelper.fractionalPart((double)(cameraPos.y * 0.5)));
        float l = k + f;
        if (this.shouldRefreshBuffers(state)) {
            this.refreshDirectionBuffer(state, viewDistanceBlocks, e, d, f, l, k);
        }
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture abstractTexture = textureManager.getTexture(FORCEFIELD);
        RenderPipeline renderPipeline = RenderPipelines.RENDERTYPE_WORLD_BORDER;
        Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        Framebuffer framebuffer2 = MinecraftClient.getInstance().worldRenderer.getWeatherFramebuffer();
        if (framebuffer2 != null) {
            gpuTextureView = framebuffer2.getColorAttachmentView();
            gpuTextureView2 = framebuffer2.getDepthAttachmentView();
        } else {
            gpuTextureView = framebuffer.getColorAttachmentView();
            gpuTextureView2 = framebuffer.getDepthAttachmentView();
        }
        GpuBuffer gpuBuffer = this.indexBuffer.getIndexBuffer(6);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(g, h, i, (float)state.alpha), (Vector3fc)new Vector3f((float)(this.lastUploadedBoundWest - d), (float)(-cameraPos.y), (float)(this.lastUploadedBoundNorth - e)), (Matrix4fc)new Matrix4f().translation(j, j, 0.0f));
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "World border", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setIndexBuffer(gpuBuffer, this.indexBuffer.getIndexType());
            renderPass.bindTexture("Sampler0", abstractTexture.getGlTextureView(), abstractTexture.getSampler());
            renderPass.setVertexBuffer(0, this.vertexBuffer);
            ArrayList<RenderPass.RenderObject> arrayList = new ArrayList<RenderPass.RenderObject>();
            for (WorldBorderRenderState.Distance distance : state.nearestBorder(d, e)) {
                if (!(distance.value() < viewDistanceBlocks)) continue;
                int m = distance.direction().getHorizontalQuarterTurns();
                arrayList.add(new RenderPass.RenderObject(0, this.vertexBuffer, gpuBuffer, this.indexBuffer.getIndexType(), 6 * m, 6));
            }
            renderPass.drawMultipleIndexed(arrayList, null, null, Collections.emptyList(), (Object)this);
        }
    }

    public void markBuffersDirty() {
        this.forceRefreshBuffers = true;
    }

    private boolean shouldRefreshBuffers(WorldBorderRenderState state) {
        return this.forceRefreshBuffers || state.minX != this.lastXMin || state.minZ != this.lastZMin || state.maxX != this.lastXMax || state.maxZ != this.lastZMax;
    }
}

