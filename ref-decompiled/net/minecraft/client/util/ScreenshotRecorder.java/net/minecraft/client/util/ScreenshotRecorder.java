/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ScreenshotRecorder {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String SCREENSHOTS_DIRECTORY = "screenshots";

    public static void saveScreenshot(File gameDirectory, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
        ScreenshotRecorder.saveScreenshot(gameDirectory, null, framebuffer, 1, messageReceiver);
    }

    public static void saveScreenshot(File gameDirectory, @Nullable String fileName, Framebuffer framebuffer, int downscaleFactor, Consumer<Text> messageReceiver) {
        ScreenshotRecorder.takeScreenshot(framebuffer, downscaleFactor, image -> {
            File file2 = new File(gameDirectory, SCREENSHOTS_DIRECTORY);
            file2.mkdir();
            File file3 = fileName == null ? ScreenshotRecorder.getScreenshotFilename(file2) : new File(file2, fileName);
            Util.getIoWorkerExecutor().execute(() -> {
                try (NativeImage nativeImage2 = image;){
                    image.writeTo(file3);
                    MutableText text = Text.literal(file3.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent.OpenFile(file3.getAbsoluteFile())));
                    messageReceiver.accept(Text.translatable("screenshot.success", text));
                }
                catch (Exception exception) {
                    LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
                    messageReceiver.accept(Text.translatable("screenshot.failure", exception.getMessage()));
                }
            });
        });
    }

    public static void takeScreenshot(Framebuffer framebuffer, Consumer<NativeImage> callback) {
        ScreenshotRecorder.takeScreenshot(framebuffer, 1, callback);
    }

    public static void takeScreenshot(Framebuffer framebuffer, int downscaleFactor, Consumer<NativeImage> callback) {
        int i = framebuffer.textureWidth;
        int j = framebuffer.textureHeight;
        GpuTexture gpuTexture = framebuffer.getColorAttachment();
        if (gpuTexture == null) {
            throw new IllegalStateException("Tried to capture screenshot of an incomplete framebuffer");
        }
        if (i % downscaleFactor != 0 || j % downscaleFactor != 0) {
            throw new IllegalArgumentException("Image size is not divisible by downscale factor");
        }
        GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Screenshot buffer", 9, (long)i * (long)j * (long)gpuTexture.getFormat().pixelSize());
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(gpuTexture, gpuBuffer, 0L, () -> {
            try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(gpuBuffer, true, false);){
                int l = j / downscaleFactor;
                int m = i / downscaleFactor;
                NativeImage nativeImage = new NativeImage(m, l, false);
                for (int n = 0; n < l; ++n) {
                    for (int o = 0; o < m; ++o) {
                        int s;
                        int p;
                        if (downscaleFactor == 1) {
                            p = mappedView.data().getInt((o + n * i) * gpuTexture.getFormat().pixelSize());
                            nativeImage.setColor(o, j - n - 1, p | 0xFF000000);
                            continue;
                        }
                        p = 0;
                        int q = 0;
                        int r = 0;
                        for (s = 0; s < downscaleFactor; ++s) {
                            for (int t = 0; t < downscaleFactor; ++t) {
                                int u = mappedView.data().getInt((o * downscaleFactor + s + (n * downscaleFactor + t) * i) * gpuTexture.getFormat().pixelSize());
                                p += ColorHelper.getRed(u);
                                q += ColorHelper.getGreen(u);
                                r += ColorHelper.getBlue(u);
                            }
                        }
                        s = downscaleFactor * downscaleFactor;
                        nativeImage.setColor(o, l - n - 1, ColorHelper.getArgb(255, p / s, q / s, r / s));
                    }
                }
                callback.accept(nativeImage);
            }
            gpuBuffer.close();
        }, 0);
    }

    private static File getScreenshotFilename(File directory) {
        String string = Util.getFormattedCurrentTime();
        int i = 1;
        File file;
        while ((file = new File(directory, string + (String)(i == 1 ? "" : "_" + i) + ".png")).exists()) {
            ++i;
        }
        return file;
    }
}
