/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$ShapeIndexBuffer
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.BillboardParticleSubmittable
 *  net.minecraft.client.particle.BillboardParticleSubmittable$Buffers
 *  net.minecraft.client.particle.BillboardParticleSubmittable$Layer
 *  net.minecraft.client.particle.BillboardParticleSubmittable$Vertices
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Submittable
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.command.LayeredCustomCommandRenderer$VerticesCache
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue$LayeredCustom
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.BufferAllocator
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Submittable;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.command.LayeredCustomCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BillboardParticleSubmittable
implements OrderedRenderCommandQueue.LayeredCustom,
Submittable {
    private static final int INITIAL_BUFFER_MAX_LENGTH = 1024;
    private static final int BUFFER_FLOAT_FIELDS = 12;
    private static final int BUFFER_INT_FIELDS = 2;
    private final Map<BillboardParticle.RenderType, Vertices> bufferByType = new HashMap();
    private int particles;

    public void render(BillboardParticle.RenderType renderType2, float x, float y, float z, float rotationX, float rotationY, float rotationZ, float rotationW, float size, float minU, float maxU, float minV, float maxV, int color, int brightness) {
        this.bufferByType.computeIfAbsent(renderType2, renderType -> new Vertices()).vertex(x, y, z, rotationX, rotationY, rotationZ, rotationW, size, minU, maxU, minV, maxV, color, brightness);
        ++this.particles;
    }

    public void onFrameEnd() {
        this.bufferByType.values().forEach(Vertices::reset);
        this.particles = 0;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BillboardParticleSubmittable.Buffers submit(LayeredCustomCommandRenderer.VerticesCache cache) {
        int i = this.particles * 4;
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized((int)(i * VertexFormats.POSITION_TEXTURE_COLOR_LIGHT.getVertexSize()));){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
            HashMap<BillboardParticle.RenderType, Layer> map = new HashMap<BillboardParticle.RenderType, Layer>();
            int j = 0;
            for (Map.Entry entry : this.bufferByType.entrySet()) {
                ((Vertices)entry.getValue()).render((x, y, z, rotationX, rotationY, rotationZ, rotationW, size, minU, maxU, minV, maxV, color, brightness) -> this.drawFace((VertexConsumer)bufferBuilder, x, y, z, rotationX, rotationY, rotationZ, rotationW, size, minU, maxU, minV, maxV, color, brightness));
                if (((Vertices)entry.getValue()).nextVertexIndex() > 0) {
                    map.put((BillboardParticle.RenderType)entry.getKey(), new Layer(j, ((Vertices)entry.getValue()).nextVertexIndex() * 6));
                }
                j += ((Vertices)entry.getValue()).nextVertexIndex() * 4;
            }
            BuiltBuffer builtBuffer = bufferBuilder.endNullable();
            if (builtBuffer != null) {
                cache.write(builtBuffer.getBuffer());
                RenderSystem.getSequentialBuffer((VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS).getIndexBuffer(builtBuffer.getDrawParameters().indexCount());
                GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
                Buffers buffers = new Buffers(builtBuffer.getDrawParameters().indexCount(), gpuBufferSlice, map);
                return buffers;
            }
            Buffers buffers = null;
            return buffers;
        }
    }

    public void render(Buffers buffers, LayeredCustomCommandRenderer.VerticesCache cache, RenderPass renderPass, TextureManager manager, boolean translucent) {
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer((VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS);
        renderPass.setVertexBuffer(0, cache.get());
        renderPass.setIndexBuffer(shapeIndexBuffer.getIndexBuffer(buffers.indexCount), shapeIndexBuffer.getIndexType());
        renderPass.setUniform("DynamicTransforms", buffers.dynamicTransforms);
        for (Map.Entry entry : buffers.layers.entrySet()) {
            if (translucent != ((BillboardParticle.RenderType)entry.getKey()).translucent()) continue;
            renderPass.setPipeline(((BillboardParticle.RenderType)entry.getKey()).pipeline());
            AbstractTexture abstractTexture = manager.getTexture(((BillboardParticle.RenderType)entry.getKey()).textureAtlasLocation());
            renderPass.bindTexture("Sampler0", abstractTexture.getGlTextureView(), abstractTexture.getSampler());
            renderPass.drawIndexed(((Layer)entry.getValue()).vertexOffset, 0, ((Layer)entry.getValue()).indexCount, 1);
        }
    }

    protected void drawFace(VertexConsumer vertexConsumer, float x, float y, float z, float rotationX, float rotationY, float rotationZ, float rotationW, float size, float minU, float maxU, float minV, float maxV, int color, int brightness) {
        Quaternionf quaternionf = new Quaternionf(rotationX, rotationY, rotationZ, rotationW);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, 1.0f, -1.0f, size, maxU, maxV, color, brightness);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, 1.0f, 1.0f, size, maxU, minV, color, brightness);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, -1.0f, 1.0f, size, minU, minV, color, brightness);
        this.renderVertex(vertexConsumer, quaternionf, x, y, z, -1.0f, -1.0f, size, minU, maxV, color, brightness);
    }

    private void renderVertex(VertexConsumer vertexConsumer, Quaternionf rotation, float x, float y, float z, float localX, float localY, float size, float maxU, float maxV, int color, int brightness) {
        Vector3f vector3f = new Vector3f(localX, localY, 0.0f).rotate((Quaternionfc)rotation).mul(size).add(x, y, z);
        vertexConsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z()).texture(maxU, maxV).color(color).light(brightness);
    }

    public void submit(OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (this.particles > 0) {
            orderedRenderCommandQueue.submitCustom((OrderedRenderCommandQueue.LayeredCustom)this);
        }
    }
}

