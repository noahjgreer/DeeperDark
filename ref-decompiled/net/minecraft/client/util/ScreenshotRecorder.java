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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ScreenshotRecorder {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String SCREENSHOTS_DIRECTORY = "screenshots";

   public static void saveScreenshot(File gameDirectory, Framebuffer framebuffer, Consumer messageReceiver) {
      saveScreenshot(gameDirectory, (String)null, framebuffer, 1, messageReceiver);
   }

   public static void saveScreenshot(File gameDirectory, @Nullable String fileName, Framebuffer framebuffer, int downscaleFactor, Consumer messageReceiver) {
      takeScreenshot(framebuffer, downscaleFactor, (image) -> {
         File file2 = new File(gameDirectory, "screenshots");
         file2.mkdir();
         File file3;
         if (fileName == null) {
            file3 = getScreenshotFilename(file2);
         } else {
            file3 = new File(file2, fileName);
         }

         Util.getIoWorkerExecutor().execute(() -> {
            try {
               NativeImage var3 = image;

               try {
                  image.writeTo(file3);
                  Text text = Text.literal(file3.getName()).formatted(Formatting.UNDERLINE).styled((style) -> {
                     return style.withClickEvent(new ClickEvent.OpenFile(file3.getAbsoluteFile()));
                  });
                  messageReceiver.accept(Text.translatable("screenshot.success", text));
               } catch (Throwable var7) {
                  if (image != null) {
                     try {
                        var3.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (image != null) {
                  image.close();
               }
            } catch (Exception var8) {
               LOGGER.warn("Couldn't save screenshot", var8);
               messageReceiver.accept(Text.translatable("screenshot.failure", var8.getMessage()));
            }

         });
      });
   }

   public static void takeScreenshot(Framebuffer framebuffer, Consumer callback) {
      takeScreenshot(framebuffer, 1, callback);
   }

   public static void takeScreenshot(Framebuffer framebuffer, int downscaleFactor, Consumer callback) {
      int i = framebuffer.textureWidth;
      int j = framebuffer.textureHeight;
      GpuTexture gpuTexture = framebuffer.getColorAttachment();
      if (gpuTexture == null) {
         throw new IllegalStateException("Tried to capture screenshot of an incomplete framebuffer");
      } else if (i % downscaleFactor == 0 && j % downscaleFactor == 0) {
         GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> {
            return "Screenshot buffer";
         }, 9, i * j * gpuTexture.getFormat().pixelSize());
         CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
         RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(gpuTexture, gpuBuffer, 0, () -> {
            GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(gpuBuffer, true, false);

            try {
               int l = j / downscaleFactor;
               int m = i / downscaleFactor;
               NativeImage nativeImage = new NativeImage(m, l, false);

               for(int n = 0; n < l; ++n) {
                  for(int o = 0; o < m; ++o) {
                     int p;
                     if (downscaleFactor == 1) {
                        p = mappedView.data().getInt((o + n * i) * gpuTexture.getFormat().pixelSize());
                        nativeImage.setColor(o, j - n - 1, p | -16777216);
                     } else {
                        p = 0;
                        int q = 0;
                        int r = 0;

                        int s;
                        for(s = 0; s < downscaleFactor; ++s) {
                           for(int t = 0; t < downscaleFactor; ++t) {
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
               }

               callback.accept(nativeImage);
            } catch (Throwable var20) {
               if (mappedView != null) {
                  try {
                     mappedView.close();
                  } catch (Throwable var19) {
                     var20.addSuppressed(var19);
                  }
               }

               throw var20;
            }

            if (mappedView != null) {
               mappedView.close();
            }

            gpuBuffer.close();
         }, 0);
      } else {
         throw new IllegalArgumentException("Image size is not divisible by downscale factor");
      }
   }

   private static File getScreenshotFilename(File directory) {
      String string = Util.getFormattedCurrentTime();
      int i = 1;

      while(true) {
         File file = new File(directory, string + (i == 1 ? "" : "_" + i) + ".png");
         if (!file.exists()) {
            return file;
         }

         ++i;
      }
   }
}
