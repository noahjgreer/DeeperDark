/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$MappedView
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.buffers.Std140SizeCalculator
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$ShapeIndexBuffer
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.Framebuffer
 *  net.minecraft.client.gl.MappableRingBuffer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.option.CloudRenderMode
 *  net.minecraft.client.render.CloudRenderer
 *  net.minecraft.client.render.CloudRenderer$CloudCells
 *  net.minecraft.client.render.CloudRenderer$ViewMode
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.SinglePreparationResourceReloader
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.profiler.Profiler
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
import net.minecraft.client.render.CloudRenderer;
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

/*
 * Exception performing whole class analysis ignored.
 */
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
    private static final Identifier CLOUD_TEXTURE = Identifier.ofVanilla((String)"textures/environment/clouds.png");
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
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable CloudRenderer.CloudCells cells;
    private int instanceCount = 0;
    private final MappableRingBuffer cloudInfoBuffer = new MappableRingBuffer(() -> "Cloud UBO", 130, UBO_SIZE);
    private @Nullable MappableRingBuffer cloudFacesBuffer;

    /*
     * Enabled aggressive exception aggregation
     */
    protected Optional<CloudCells> prepare(ResourceManager resourceManager, Profiler profiler) {
        try (InputStream inputStream = resourceManager.open(CLOUD_TEXTURE);){
            Optional<CloudCells> optional;
            block17: {
                NativeImage nativeImage = NativeImage.read((InputStream)inputStream);
                try {
                    int i = nativeImage.getWidth();
                    int j = nativeImage.getHeight();
                    long[] ls = new long[i * j];
                    for (int k = 0; k < j; ++k) {
                        for (int l = 0; l < i; ++l) {
                            int m = nativeImage.getColorArgb(l, k);
                            if (CloudRenderer.isEmpty((int)m)) {
                                ls[l + k * i] = 0L;
                                continue;
                            }
                            boolean bl = CloudRenderer.isEmpty((int)nativeImage.getColorArgb(l, Math.floorMod(k - 1, j)));
                            boolean bl2 = CloudRenderer.isEmpty((int)nativeImage.getColorArgb(Math.floorMod(l + 1, j), k));
                            boolean bl3 = CloudRenderer.isEmpty((int)nativeImage.getColorArgb(l, Math.floorMod(k + 1, j)));
                            boolean bl4 = CloudRenderer.isEmpty((int)nativeImage.getColorArgb(Math.floorMod(l - 1, j), k));
                            ls[l + k * i] = CloudRenderer.packCloudCell((int)m, (boolean)bl, (boolean)bl2, (boolean)bl3, (boolean)bl4);
                        }
                    }
                    optional = Optional.of(new CloudCells(ls, i, j));
                    if (nativeImage == null) break block17;
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
                nativeImage.close();
            }
            return optional;
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

    protected void apply(Optional<CloudCells> optional, ResourceManager resourceManager, Profiler profiler) {
        this.cells = optional.orElse(null);
        this.rebuild = true;
    }

    private static boolean isEmpty(int color) {
        return ColorHelper.getAlpha((int)color) < 10;
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
        int i = (Integer)MinecraftClient.getInstance().options.getCloudRenderDistance().getValue() * 16;
        int j = MathHelper.ceil((float)((float)i / 12.0f));
        int k = CloudRenderer.calcCloudBufferSize((int)j);
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
        d -= (double)MathHelper.floor((double)(d / n)) * n;
        e -= (double)MathHelper.floor((double)(e / o)) * o;
        int p = MathHelper.floor((double)(d / 12.0));
        int q = MathHelper.floor((double)(e / 12.0));
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
            Std140Builder.intoBuffer((ByteBuffer)mappedView.data()).putVec4((Vector4fc)ColorHelper.toRgbaVector((int)color)).putVec3(-r, g, -s).putVec3(12.0f, 4.0f, 12.0f);
        }
        finally {
            if (mappedView != null) {
                mappedView.close();
            }
        }
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        Framebuffer framebuffer2 = MinecraftClient.getInstance().worldRenderer.getCloudsFramebuffer();
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer((VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS);
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
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
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
        if (CloudRenderer.hasBorderNorth((long)l) && j > 0) {
            this.method_71098(byteBuffer, i, j, Direction.NORTH, 0);
        }
        if (CloudRenderer.hasBorderSouth((long)l) && j < 0) {
            this.method_71098(byteBuffer, i, j, Direction.SOUTH, 0);
        }
        if (CloudRenderer.hasBorderWest((long)l) && i > 0) {
            this.method_71098(byteBuffer, i, j, Direction.WEST, 0);
        }
        if (CloudRenderer.hasBorderEast((long)l) && i < 0) {
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

    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

