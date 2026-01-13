/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$MappedView
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.util.annotation.DeobfuscateClass
 *  net.minecraft.util.math.ColorHelper
 *  org.lwjgl.system.MemoryUtil
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.annotation.DeobfuscateClass;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class TextureUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int MIN_MIPMAP_LEVEL = 0;
    private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;
    private static final int[][] DIRECTIONS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    public static ByteBuffer readResource(InputStream inputStream) throws IOException {
        ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
        if (readableByteChannel instanceof SeekableByteChannel) {
            SeekableByteChannel seekableByteChannel = (SeekableByteChannel)readableByteChannel;
            return TextureUtil.readResource((ReadableByteChannel)readableByteChannel, (int)((int)seekableByteChannel.size() + 1));
        }
        return TextureUtil.readResource((ReadableByteChannel)readableByteChannel, (int)8192);
    }

    private static ByteBuffer readResource(ReadableByteChannel channel, int bufSize) throws IOException {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)bufSize);
        try {
            while (channel.read(byteBuffer) != -1) {
                if (byteBuffer.hasRemaining()) continue;
                byteBuffer = MemoryUtil.memRealloc((ByteBuffer)byteBuffer, (int)(byteBuffer.capacity() * 2));
            }
            byteBuffer.flip();
            return byteBuffer;
        }
        catch (IOException iOException) {
            MemoryUtil.memFree((Buffer)byteBuffer);
            throw iOException;
        }
    }

    public static void writeAsPNG(Path directory, String prefix, GpuTexture texture, int scales, IntUnaryOperator colorFunction) {
        RenderSystem.assertOnRenderThread();
        long l = 0L;
        for (int i = 0; i <= scales; ++i) {
            l += (long)texture.getFormat().pixelSize() * (long)texture.getWidth(i) * (long)texture.getHeight(i);
        }
        if (l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Exporting textures larger than 2GB is not supported");
        }
        GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Texture output buffer", 9, l);
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        Runnable runnable = () -> {
            try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(gpuBuffer, true, false);){
                int j = 0;
                for (int k = 0; k <= scales; ++k) {
                    int l = texture.getWidth(k);
                    int m = texture.getHeight(k);
                    try (NativeImage nativeImage = new NativeImage(l, m, false);){
                        for (int n = 0; n < m; ++n) {
                            for (int o = 0; o < l; ++o) {
                                int p = mappedView.data().getInt(j + (o + n * l) * texture.getFormat().pixelSize());
                                nativeImage.setColor(o, n, colorFunction.applyAsInt(p));
                            }
                        }
                        Path path2 = directory.resolve(prefix + "_" + k + ".png");
                        nativeImage.writeTo(path2);
                        LOGGER.debug("Exported png to: {}", (Object)path2.toAbsolutePath());
                    }
                    catch (IOException iOException) {
                        LOGGER.debug("Unable to write: ", (Throwable)iOException);
                    }
                    j += texture.getFormat().pixelSize() * l * m;
                }
            }
            gpuBuffer.close();
        };
        AtomicInteger atomicInteger = new AtomicInteger();
        int j = 0;
        for (int k = 0; k <= scales; ++k) {
            commandEncoder.copyTextureToBuffer(texture, gpuBuffer, (long)j, () -> {
                if (atomicInteger.getAndIncrement() == scales) {
                    runnable.run();
                }
            }, k);
            j += texture.getFormat().pixelSize() * texture.getWidth(k) * texture.getHeight(k);
        }
    }

    public static Path getDebugTexturePath(Path path) {
        return path.resolve("screenshots").resolve("debug");
    }

    public static Path getDebugTexturePath() {
        return TextureUtil.getDebugTexturePath((Path)Path.of(".", new String[0]));
    }

    public static void solidify(NativeImage image) {
        int m;
        int l;
        int k;
        int i = image.getWidth();
        int j = image.getHeight();
        int[] is = new int[i * j];
        int[] js = new int[i * j];
        Arrays.fill(js, Integer.MAX_VALUE);
        IntArrayFIFOQueue intArrayFIFOQueue = new IntArrayFIFOQueue();
        for (k = 0; k < i; ++k) {
            for (l = 0; l < j; ++l) {
                m = image.getColorArgb(k, l);
                if (ColorHelper.getAlpha((int)m) == 0) continue;
                int n = TextureUtil.pack((int)k, (int)l, (int)i);
                js[n] = 0;
                is[n] = m;
                intArrayFIFOQueue.enqueue(n);
            }
        }
        while (!intArrayFIFOQueue.isEmpty()) {
            k = intArrayFIFOQueue.dequeueInt();
            l = TextureUtil.x((int)k, (int)i);
            m = TextureUtil.y((int)k, (int)i);
            for (int[] ks : DIRECTIONS) {
                int o = l + ks[0];
                int p = m + ks[1];
                int q = TextureUtil.pack((int)o, (int)p, (int)i);
                if (o < 0 || p < 0 || o >= i || p >= j || js[q] <= js[k] + 1) continue;
                js[q] = js[k] + 1;
                is[q] = is[k];
                intArrayFIFOQueue.enqueue(q);
            }
        }
        for (k = 0; k < i; ++k) {
            for (l = 0; l < j; ++l) {
                m = image.getColorArgb(k, l);
                if (ColorHelper.getAlpha((int)m) == 0) {
                    image.setColorArgb(k, l, ColorHelper.withAlpha((int)0, (int)is[TextureUtil.pack((int)k, (int)l, (int)i)]));
                    continue;
                }
                image.setColorArgb(k, l, m);
            }
        }
    }

    public static void fillEmptyAreasWithDarkColor(NativeImage image) {
        int s;
        int r;
        int q;
        int p;
        int o;
        int n;
        int m;
        int i = image.getWidth();
        int j = image.getHeight();
        int k = -1;
        int l = Integer.MAX_VALUE;
        for (m = 0; m < i; ++m) {
            for (n = 0; n < j; ++n) {
                int t;
                o = image.getColorArgb(m, n);
                p = ColorHelper.getAlpha((int)o);
                if (p == 0 || (t = (q = ColorHelper.getRed((int)o)) + (r = ColorHelper.getGreen((int)o)) + (s = ColorHelper.getBlue((int)o))) >= l) continue;
                l = t;
                k = o;
            }
        }
        m = 3 * ColorHelper.getRed((int)k) / 4;
        n = 3 * ColorHelper.getGreen((int)k) / 4;
        o = 3 * ColorHelper.getBlue((int)k) / 4;
        p = ColorHelper.getArgb((int)0, (int)m, (int)n, (int)o);
        for (q = 0; q < i; ++q) {
            for (r = 0; r < j; ++r) {
                s = image.getColorArgb(q, r);
                if (ColorHelper.getAlpha((int)s) != 0) continue;
                image.setColorArgb(q, r, p);
            }
        }
    }

    private static int pack(int x, int y, int width) {
        return x + y * width;
    }

    private static int x(int packed, int width) {
        return packed % width;
    }

    private static int y(int packed, int width) {
        return packed / width;
    }
}

