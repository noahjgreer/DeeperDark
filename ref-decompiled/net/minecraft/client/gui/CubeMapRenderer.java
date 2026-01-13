/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.ProjectionType
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$ShapeIndexBuffer
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.CubeMapRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.ProjectionMatrix3
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.CubemapTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.BufferAllocator
 *  net.minecraft.util.Identifier
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.gui;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.ProjectionMatrix3;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.CubemapTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CubeMapRenderer
implements AutoCloseable {
    private static final int FACES_COUNT = 6;
    private final GpuBuffer buffer;
    private final ProjectionMatrix3 projectionMatrix;
    private final Identifier id;

    public CubeMapRenderer(Identifier id) {
        this.id = id;
        this.projectionMatrix = new ProjectionMatrix3("cubemap", 0.05f, 10.0f);
        this.buffer = CubeMapRenderer.upload();
    }

    public void draw(MinecraftClient client, float x, float y) {
        RenderSystem.setProjectionMatrix((GpuBufferSlice)this.projectionMatrix.set(client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), 85.0f), (ProjectionType)ProjectionType.PERSPECTIVE);
        RenderPipeline renderPipeline = RenderPipelines.POSITION_TEX_PANORAMA;
        Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        GpuTextureView gpuTextureView = framebuffer.getColorAttachmentView();
        GpuTextureView gpuTextureView2 = framebuffer.getDepthAttachmentView();
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer((VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(36);
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.rotationX((float)Math.PI);
        matrix4fStack.rotateX(x * ((float)Math.PI / 180));
        matrix4fStack.rotateY(y * ((float)Math.PI / 180));
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)new Matrix4f((Matrix4fc)matrix4fStack), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        matrix4fStack.popMatrix();
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Cubemap", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setVertexBuffer(0, this.buffer);
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            AbstractTexture abstractTexture = client.getTextureManager().getTexture(this.id);
            renderPass.bindTexture("Sampler0", abstractTexture.getGlTextureView(), abstractTexture.getSampler());
            renderPass.drawIndexed(0, 0, 36, 1);
        }
    }

    private static GpuBuffer upload() {
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized((int)(VertexFormats.POSITION.getVertexSize() * 4 * 6));){
            GpuBuffer gpuBuffer;
            block12: {
                BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
                bufferBuilder.vertex(-1.0f, -1.0f, 1.0f);
                bufferBuilder.vertex(-1.0f, 1.0f, 1.0f);
                bufferBuilder.vertex(1.0f, 1.0f, 1.0f);
                bufferBuilder.vertex(1.0f, -1.0f, 1.0f);
                bufferBuilder.vertex(1.0f, -1.0f, 1.0f);
                bufferBuilder.vertex(1.0f, 1.0f, 1.0f);
                bufferBuilder.vertex(1.0f, 1.0f, -1.0f);
                bufferBuilder.vertex(1.0f, -1.0f, -1.0f);
                bufferBuilder.vertex(1.0f, -1.0f, -1.0f);
                bufferBuilder.vertex(1.0f, 1.0f, -1.0f);
                bufferBuilder.vertex(-1.0f, 1.0f, -1.0f);
                bufferBuilder.vertex(-1.0f, -1.0f, -1.0f);
                bufferBuilder.vertex(-1.0f, -1.0f, -1.0f);
                bufferBuilder.vertex(-1.0f, 1.0f, -1.0f);
                bufferBuilder.vertex(-1.0f, 1.0f, 1.0f);
                bufferBuilder.vertex(-1.0f, -1.0f, 1.0f);
                bufferBuilder.vertex(-1.0f, -1.0f, -1.0f);
                bufferBuilder.vertex(-1.0f, -1.0f, 1.0f);
                bufferBuilder.vertex(1.0f, -1.0f, 1.0f);
                bufferBuilder.vertex(1.0f, -1.0f, -1.0f);
                bufferBuilder.vertex(-1.0f, 1.0f, 1.0f);
                bufferBuilder.vertex(-1.0f, 1.0f, -1.0f);
                bufferBuilder.vertex(1.0f, 1.0f, -1.0f);
                bufferBuilder.vertex(1.0f, 1.0f, 1.0f);
                BuiltBuffer builtBuffer = bufferBuilder.end();
                try {
                    gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Cube map vertex buffer", 32, builtBuffer.getBuffer());
                    if (builtBuffer == null) break block12;
                }
                catch (Throwable throwable) {
                    if (builtBuffer != null) {
                        try {
                            builtBuffer.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                builtBuffer.close();
            }
            return gpuBuffer;
        }
    }

    public void registerTextures(TextureManager textureManager) {
        textureManager.registerTexture(this.id, (AbstractTexture)new CubemapTexture(this.id));
    }

    @Override
    public void close() {
        this.buffer.close();
        this.projectionMatrix.close();
    }
}

