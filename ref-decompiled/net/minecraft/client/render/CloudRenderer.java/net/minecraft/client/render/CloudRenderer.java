/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CloudRenderer
extends SinglePreparationResourceReloader<Optional<CloudCells>>
implements AutoCloseable {
    private static final int field_60075 = 16;
    private static final int field_60076 = 32;
    private static final float field_53043 = 12.0f;
    private static final int field_64448 = 400;
    private static final float field_53045 = 0.6f;
    private static final int UBO_SIZE = new Std140SizeCalculator().putVec4().putVec3().putVec3().get();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Identifier CLOUD_TEXTURE = Identifier.ofVanilla("textures/environment/clouds.png");
    private static final long field_53046 = 0L;
    private static final int field_53047 = 4;
    private static final int field_53048 = 3;
    private static final int field_53049 = 2;
    private static final int field_53050 = 1;
    private static final int field_53051 = 0;
    private boolean rebuild = true;
    private int centerX = Integer.MIN_VALUE;
    private int centerZ = Integer.MIN_VALUE;
    private ViewMode viewMode = ViewMode.INSIDE_CLOUDS;
    private @Nullable CloudRenderMode renderMode;
    private @Nullable CloudCells cells;
    private int instanceCount = 0;
    private final MappableRingBuffer cloudInfoBuffer = new MappableRingBuffer(() -> "Cloud UBO", 130, UBO_SIZE);
    private @Nullable MappableRingBuffer cloudFacesBuffer;

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    protected Optional<CloudCells> prepare(ResourceManager resourceManager, Profiler profiler) {
        try (InputStream inputStream = resourceManager.open(CLOUD_TEXTURE);){
            NativeImage nativeImage = NativeImage.read(inputStream);
            try {
                int i = nativeImage.getWidth();
                int j = nativeImage.getHeight();
                long[] ls = new long[i * j];
                for (int k = 0; k < j; ++k) {
                    for (int l = 0; l < i; ++l) {
                        int m = nativeImage.getColorArgb(l, k);
                        if (CloudRenderer.isEmpty(m)) {
                            ls[l + k * i] = 0L;
                            continue;
                        }
                        boolean bl = CloudRenderer.isEmpty(nativeImage.getColorArgb(l, Math.floorMod(k - 1, j)));
                        boolean bl2 = CloudRenderer.isEmpty(nativeImage.getColorArgb(Math.floorMod(l + 1, j), k));
                        boolean bl3 = CloudRenderer.isEmpty(nativeImage.getColorArgb(l, Math.floorMod(k + 1, j)));
                        boolean bl4 = CloudRenderer.isEmpty(nativeImage.getColorArgb(Math.floorMod(l - 1, j), k));
                        ls[l + k * i] = CloudRenderer.packCloudCell(m, bl, bl2, bl3, bl4);
                    }
                }
                Optional<CloudCells> optional = Optional.of(new CloudCells(ls, i, j));
                if (nativeImage != null) {
                    nativeImage.close();
                }
                return optional;
            }
            catch (Throwable throwable) {
                if (nativeImage != null) {
                    try {
                        nativeImage.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Failed to load cloud texture", (Throwable)iOException);
            return Optional.empty();
        }
    }

    private static int calcCloudBufferSize(int cloudRange) {
        int i = 4;
        int j = (cloudRange + 1) * 2 * ((cloudRange + 1) * 2) / 2;
        int k = j * 4 + 54;
        return k * 3;
    }

    @Override
    protected void apply(Optional<CloudCells> optional, ResourceManager resourceManager, Profiler profiler) {
        this.cells = optional.orElse(null);
        this.rebuild = true;
    }

    private static boolean isEmpty(int color) {
        return ColorHelper.getAlpha(color) < 10;
    }

    private static long packCloudCell(int color, boolean borderNorth, boolean borderEast, boolean borderSouth, boolean borderWest) {
        return (long)color << 4 | (long)((borderNorth ? 1 : 0) << 3) | (long)((borderEast ? 1 : 0) << 2) | (long)((borderSouth ? 1 : 0) << 1) | (long)((borderWest ? 1 : 0) << 0);
    }

    private static boolean hasBorderNorth(long packed) {
        return (packed >> 3 & 1L) != 0L;
    }

    private static boolean hasBorderEast(long packed) {
        return (packed >> 2 & 1L) != 0L;
    }

    private static boolean hasBorderSouth(long packed) {
        return (packed >> 1 & 1L) != 0L;
    }

    private static boolean hasBorderWest(long packed) {
        return (packed >> 0 & 1L) != 0L;
    }

    public void renderClouds(int color, CloudRenderMode mode, float cloudHeight, Vec3d vec3d, long l, float f) {
        GpuTextureView gpuTextureView2;
        GpuTextureView gpuTextureView;
        GpuBuffer.MappedView mappedView;
        RenderPipeline renderPipeline;
        float g;
        float h;
        if (this.cells == null) {
            return;
        }
        int i = MinecraftClient.getInstance().options.getCloudRenderDistance().getValue() * 16;
        int j = MathHelper.ceil((float)i / 12.0f);
        int k = CloudRenderer.calcCloudBufferSize(j);
        if (this.cloudFacesBuffer == null || this.cloudFacesBuffer.getBlocking().size() != (long)k) {
            if (this.cloudFacesBuffer != null) {
                this.cloudFacesBuffer.close();
            }
            this.cloudFacesBuffer = new MappableRingBuffer(() -> "Cloud UTB", 258, k);
        }
        ViewMode viewMode = (h = (g = (float)((double)cloudHeight - vec3d.y)) + 4.0f) < 0.0f ? ViewMode.ABOVE_CLOUDS : (g > 0.0f ? ViewMode.BELOW_CLOUDS : ViewMode.INSIDE_CLOUDS);
        float m = (float)(l % ((long)this.cells.width * 400L)) + f;
        double d = vec3d.x + (double)(m * 0.030000001f);
        double e = vec3d.z + (double)3.96f;
        double n = (double)this.cells.width * 12.0;
        double o = (double)this.cells.height * 12.0;
        d -= (double)MathHelper.floor(d / n) * n;
        e -= (double)MathHelper.floor(e / o) * o;
        int p = MathHelper.floor(d / 12.0);
        int q = MathHelper.floor(e / 12.0);
        float r = (float)(d - (double)((float)p * 12.0f));
        float s = (float)(e - (double)((float)q * 12.0f));
        boolean bl = mode == CloudRenderMode.FANCY;
        RenderPipeline renderPipeline2 = renderPipeline = bl ? RenderPipelines.CLOUDS : RenderPipelines.FLAT_CLOUDS;
        if (this.rebuild || p != this.centerX || q != this.centerZ || viewMode != this.viewMode || mode != this.renderMode) {
            this.rebuild = false;
            this.centerX = p;
            this.centerZ = q;
            this.viewMode = viewMode;
            this.renderMode = mode;
            this.cloudFacesBuffer.rotate();
            mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.cloudFacesBuffer.getBlocking(), false, true);
            try {
                this.buildCloudCells(viewMode, mappedView.data(), p, q, bl, j);
                this.instanceCount = mappedView.data().position() / 3;
            }
            finally {
                if (mappedView != null) {
                    mappedView.close();
                }
            }
        }
        if (this.instanceCount == 0) {
            return;
        }
        mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.cloudInfoBuffer.getBlocking(), false, true);
        try {
            Std140Builder.intoBuffer(mappedView.data()).putVec4((Vector4fc)ColorHelper.toRgbaVector(color)).putVec3(-r, g, -s).putVec3(12.0f, 4.0f, 12.0f);
        }
        finally {
            if (mappedView != null) {
                mappedView.close();
            }
        }
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        Framebuffer framebuffer2 = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer();
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(6 * this.instanceCount);
        if (framebuffer2 != null) {
            gpuTextureView = framebuffer2.getColorAttachmentView();
            gpuTextureView2 = framebuffer2.getDepthAttachmentView();
        } else {
            gpuTextureView = framebuffer.getColorAttachmentView();
            gpuTextureView2 = framebuffer.getDepthAttachmentView();
        }
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Clouds", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.setUniform("CloudInfo", this.cloudInfoBuffer.getBlocking());
            renderPass.setUniform("CloudFaces", this.cloudFacesBuffer.getBlocking());
            renderPass.drawIndexed(0, 0, 6 * this.instanceCount, 1);
        }
    }

    private void buildCloudCells(ViewMode viewMode, ByteBuffer byteBuffer, int x, int z, boolean bl, int i) {
        if (this.cells == null) {
            return;
        }
        long[] ls = this.cells.cells;
        int j = this.cells.width;
        int k = this.cells.height;
        for (int l = 0; l <= 2 * i; ++l) {
            for (int m = -l; m <= l; ++m) {
                int n = l - Math.abs(m);
                if (n < 0 || n > i || m * m + n * n > i * i) continue;
                if (n != 0) {
                    this.method_72155(viewMode, byteBuffer, x, z, bl, m, j, -n, k, ls);
                }
                this.method_72155(viewMode, byteBuffer, x, z, bl, m, j, n, k, ls);
            }
        }
    }

    private void method_72155(ViewMode viewMode, ByteBuffer byteBuffer, int i, int j, boolean bl, int k, int l, int m, int n, long[] ls) {
        int p;
        int o = Math.floorMod(i + k, l);
        long q = ls[o + (p = Math.floorMod(j + m, n)) * l];
        if (q == 0L) {
            return;
        }
        if (bl) {
            this.buildCloudCellFancy(viewMode, byteBuffer, k, m, q);
        } else {
            this.buildCloudCellFast(byteBuffer, k, m);
        }
    }

    private void buildCloudCellFast(ByteBuffer byteBuffer, int color, int x) {
        this.method_71098(byteBuffer, color, x, Direction.DOWN, 32);
    }

    private void method_71098(ByteBuffer byteBuffer, int i, int j, Direction direction, int k) {
        int l = direction.getIndex() | k;
        l |= (i & 1) << 7;
        byteBuffer.put((byte)(i >> 1)).put((byte)(j >> 1)).put((byte)(l |= (j & 1) << 6));
    }

    private void buildCloudCellFancy(ViewMode viewMode, ByteBuffer byteBuffer, int i, int j, long l) {
        boolean bl;
        if (viewMode != ViewMode.BELOW_CLOUDS) {
            this.method_71098(byteBuffer, i, j, Direction.UP, 0);
        }
        if (viewMode != ViewMode.ABOVE_CLOUDS) {
            this.method_71098(byteBuffer, i, j, Direction.DOWN, 0);
        }
        if (CloudRenderer.hasBorderNorth(l) && j > 0) {
            this.method_71098(byteBuffer, i, j, Direction.NORTH, 0);
        }
        if (CloudRenderer.hasBorderSouth(l) && j < 0) {
            this.method_71098(byteBuffer, i, j, Direction.SOUTH, 0);
        }
        if (CloudRenderer.hasBorderWest(l) && i > 0) {
            this.method_71098(byteBuffer, i, j, Direction.WEST, 0);
        }
        if (CloudRenderer.hasBorderEast(l) && i < 0) {
            this.method_71098(byteBuffer, i, j, Direction.EAST, 0);
        }
        boolean bl2 = bl = Math.abs(i) <= 1 && Math.abs(j) <= 1;
        if (bl) {
            for (Direction direction : Direction.values()) {
                this.method_71098(byteBuffer, i, j, direction, 16);
            }
        }
    }

    public void scheduleTerrainUpdate() {
        this.rebuild = true;
    }

    public void rotate() {
        this.cloudInfoBuffer.rotate();
    }

    @Override
    public void close() {
        this.cloudInfoBuffer.close();
        if (this.cloudFacesBuffer != null) {
            this.cloudFacesBuffer.close();
        }
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }

    @Environment(value=EnvType.CLIENT)
    static final class ViewMode
    extends Enum<ViewMode> {
        public static final /* enum */ ViewMode ABOVE_CLOUDS = new ViewMode();
        public static final /* enum */ ViewMode INSIDE_CLOUDS = new ViewMode();
        public static final /* enum */ ViewMode BELOW_CLOUDS = new ViewMode();
        private static final /* synthetic */ ViewMode[] field_53063;

        public static ViewMode[] values() {
            return (ViewMode[])field_53063.clone();
        }

        public static ViewMode valueOf(String string) {
            return Enum.valueOf(ViewMode.class, string);
        }

        private static /* synthetic */ ViewMode[] method_62182() {
            return new ViewMode[]{ABOVE_CLOUDS, INSIDE_CLOUDS, BELOW_CLOUDS};
        }

        static {
            field_53063 = ViewMode.method_62182();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class CloudCells
    extends Record {
        final long[] cells;
        final int width;
        final int height;

        public CloudCells(long[] cells, int width, int height) {
            this.cells = cells;
            this.width = width;
            this.height = height;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CloudCells.class, "cells;width;height", "cells", "width", "height"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CloudCells.class, "cells;width;height", "cells", "width", "height"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CloudCells.class, "cells;width;height", "cells", "width", "height"}, this, object);
        }

        public long[] cells() {
            return this.cells;
        }

        public int width() {
            return this.width;
        }

        public int height() {
            return this.height;
        }
    }
}
