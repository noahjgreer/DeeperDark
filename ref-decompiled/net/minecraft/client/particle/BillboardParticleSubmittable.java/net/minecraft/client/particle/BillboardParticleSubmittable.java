/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
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
    private final Map<BillboardParticle.RenderType, Vertices> bufferByType = new HashMap<BillboardParticle.RenderType, Vertices>();
    private int particles;

    public void render(BillboardParticle.RenderType renderType2, float x, float y, float z, float rotationX, float rotationY, float rotationZ, float rotationW, float size, float minU, float maxU, float minV, float maxV, int color, int brightness) {
        this.bufferByType.computeIfAbsent(renderType2, renderType -> new Vertices()).vertex(x, y, z, rotationX, rotationY, rotationZ, rotationW, size, minU, maxU, minV, maxV, color, brightness);
        ++this.particles;
    }

    @Override
    public void onFrameEnd() {
        this.bufferByType.values().forEach(Vertices::reset);
        this.particles = 0;
    }

    @Override
    public @Nullable Buffers submit(LayeredCustomCommandRenderer.VerticesCache cache) {
        int i = this.particles * 4;
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(i * VertexFormats.POSITION_TEXTURE_COLOR_LIGHT.getVertexSize());){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
            HashMap<BillboardParticle.RenderType, Layer> map = new HashMap<BillboardParticle.RenderType, Layer>();
            int j = 0;
            for (Map.Entry<BillboardParticle.RenderType, Vertices> entry : this.bufferByType.entrySet()) {
                entry.getValue().render((x, y, z, rotationX, rotationY, rotationZ, rotationW, size, minU, maxU, minV, maxV, color, brightness) -> this.drawFace(bufferBuilder, x, y, z, rotationX, rotationY, rotationZ, rotationW, size, minU, maxU, minV, maxV, color, brightness));
                if (entry.getValue().nextVertexIndex() > 0) {
                    map.put(entry.getKey(), new Layer(j, entry.getValue().nextVertexIndex() * 6));
                }
                j += entry.getValue().nextVertexIndex() * 4;
            }
            BuiltBuffer builtBuffer = bufferBuilder.endNullable();
            if (builtBuffer != null) {
                cache.write(builtBuffer.getBuffer());
                RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS).getIndexBuffer(builtBuffer.getDrawParameters().indexCount());
                GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
                Buffers buffers = new Buffers(builtBuffer.getDrawParameters().indexCount(), gpuBufferSlice, map);
                return buffers;
            }
            Buffers buffers = null;
            return buffers;
        }
    }

    @Override
    public void render(Buffers buffers, LayeredCustomCommandRenderer.VerticesCache cache, RenderPass renderPass, TextureManager manager, boolean translucent) {
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        renderPass.setVertexBuffer(0, cache.get());
        renderPass.setIndexBuffer(shapeIndexBuffer.getIndexBuffer(buffers.indexCount), shapeIndexBuffer.getIndexType());
        renderPass.setUniform("DynamicTransforms", buffers.dynamicTransforms);
        for (Map.Entry<BillboardParticle.RenderType, Layer> entry : buffers.layers.entrySet()) {
            if (translucent != entry.getKey().translucent()) continue;
            renderPass.setPipeline(entry.getKey().pipeline());
            AbstractTexture abstractTexture = manager.getTexture(entry.getKey().textureAtlasLocation());
            renderPass.bindTexture("Sampler0", abstractTexture.getGlTextureView(), abstractTexture.getSampler());
            renderPass.drawIndexed(entry.getValue().vertexOffset, 0, entry.getValue().indexCount, 1);
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

    @Override
    public void submit(OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (this.particles > 0) {
            orderedRenderCommandQueue.submitCustom(this);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Vertices {
        private int maxVertices = 1024;
        private float[] floatData = new float[12288];
        private int[] intData = new int[2048];
        private int nextVertexIndex;

        Vertices() {
        }

        public void vertex(float x, float y, float z, float rotationX, float rotationY, float rotationZ, float rotationW, float size, float minU, float maxU, float minV, float maxV, int color, int brightness) {
            if (this.nextVertexIndex >= this.maxVertices) {
                this.increaseCapacity();
            }
            int i = this.nextVertexIndex * 12;
            this.floatData[i++] = x;
            this.floatData[i++] = y;
            this.floatData[i++] = z;
            this.floatData[i++] = rotationX;
            this.floatData[i++] = rotationY;
            this.floatData[i++] = rotationZ;
            this.floatData[i++] = rotationW;
            this.floatData[i++] = size;
            this.floatData[i++] = minU;
            this.floatData[i++] = maxU;
            this.floatData[i++] = minV;
            this.floatData[i] = maxV;
            i = this.nextVertexIndex * 2;
            this.intData[i++] = color;
            this.intData[i] = brightness;
            ++this.nextVertexIndex;
        }

        public void render(Consumer vertexConsumer) {
            for (int i = 0; i < this.nextVertexIndex; ++i) {
                int j = i * 12;
                int k = i * 2;
                vertexConsumer.consume(this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j], this.intData[k++], this.intData[k]);
            }
        }

        public void reset() {
            this.nextVertexIndex = 0;
        }

        private void increaseCapacity() {
            this.maxVertices *= 2;
            this.floatData = Arrays.copyOf(this.floatData, this.maxVertices * 12);
            this.intData = Arrays.copyOf(this.intData, this.maxVertices * 2);
        }

        public int nextVertexIndex() {
            return this.nextVertexIndex;
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface Consumer {
        public void consume(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, int var13, int var14);
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Layer
    extends Record {
        final int vertexOffset;
        final int indexCount;

        public Layer(int vertexOffset, int indexCount) {
            this.vertexOffset = vertexOffset;
            this.indexCount = indexCount;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Layer.class, "vertexOffset;indexCount", "vertexOffset", "indexCount"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Layer.class, "vertexOffset;indexCount", "vertexOffset", "indexCount"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Layer.class, "vertexOffset;indexCount", "vertexOffset", "indexCount"}, this, object);
        }

        public int vertexOffset() {
            return this.vertexOffset;
        }

        public int indexCount() {
            return this.indexCount;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Buffers
    extends Record {
        final int indexCount;
        final GpuBufferSlice dynamicTransforms;
        final Map<BillboardParticle.RenderType, Layer> layers;

        public Buffers(int indexCount, GpuBufferSlice dynamicTransforms, Map<BillboardParticle.RenderType, Layer> layers) {
            this.indexCount = indexCount;
            this.dynamicTransforms = dynamicTransforms;
            this.layers = layers;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Buffers.class, "indexCount;dynamicTransforms;layers", "indexCount", "dynamicTransforms", "layers"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Buffers.class, "indexCount;dynamicTransforms;layers", "indexCount", "dynamicTransforms", "layers"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Buffers.class, "indexCount;dynamicTransforms;layers", "indexCount", "dynamicTransforms", "layers"}, this, object);
        }

        public int indexCount() {
            return this.indexCount;
        }

        public GpuBufferSlice dynamicTransforms() {
            return this.dynamicTransforms;
        }

        public Map<BillboardParticle.RenderType, Layer> layers() {
            return this.layers;
        }
    }
}
