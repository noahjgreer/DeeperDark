package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public class TextureUtil {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MIN_MIPMAP_LEVEL = 0;
   private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

   public static ByteBuffer readResource(InputStream inputStream) throws IOException {
      ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
      if (readableByteChannel instanceof SeekableByteChannel seekableByteChannel) {
         return readResource(readableByteChannel, (int)seekableByteChannel.size() + 1);
      } else {
         return readResource(readableByteChannel, 8192);
      }
   }

   private static ByteBuffer readResource(ReadableByteChannel channel, int bufSize) throws IOException {
      ByteBuffer byteBuffer = MemoryUtil.memAlloc(bufSize);

      try {
         while(channel.read(byteBuffer) != -1) {
            if (!byteBuffer.hasRemaining()) {
               byteBuffer = MemoryUtil.memRealloc(byteBuffer, byteBuffer.capacity() * 2);
            }
         }

         return byteBuffer;
      } catch (IOException var4) {
         MemoryUtil.memFree(byteBuffer);
         throw var4;
      }
   }

   public static void writeAsPNG(Path directory, String prefix, GpuTexture texture, int scales, IntUnaryOperator colorFunction) {
      RenderSystem.assertOnRenderThread();
      int i = 0;

      for(int j = 0; j <= scales; ++j) {
         i += texture.getFormat().pixelSize() * texture.getWidth(j) * texture.getHeight(j);
      }

      GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> {
         return "Texture output buffer";
      }, 9, i);
      CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
      Runnable runnable = () -> {
         GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(gpuBuffer, true, false);

         try {
            int j = 0;

            for(int k = 0; k <= scales; ++k) {
               int l = texture.getWidth(k);
               int m = texture.getHeight(k);

               try {
                  NativeImage nativeImage = new NativeImage(l, m, false);

                  try {
                     int n = 0;

                     while(true) {
                        if (n >= m) {
                           Path path2 = directory.resolve(prefix + "_" + k + ".png");
                           nativeImage.writeTo(path2);
                           LOGGER.debug("Exported png to: {}", path2.toAbsolutePath());
                           break;
                        }

                        for(int o = 0; o < l; ++o) {
                           int p = mappedView.data().getInt(j + (o + n * l) * texture.getFormat().pixelSize());
                           nativeImage.setColor(o, n, colorFunction.applyAsInt(p));
                        }

                        ++n;
                     }
                  } catch (Throwable var18) {
                     try {
                        nativeImage.close();
                     } catch (Throwable var17) {
                        var18.addSuppressed(var17);
                     }

                     throw var18;
                  }

                  nativeImage.close();
               } catch (IOException var19) {
                  LOGGER.debug("Unable to write: ", var19);
               }

               j += texture.getFormat().pixelSize() * l * m;
            }
         } catch (Throwable var20) {
            if (mappedView != null) {
               try {
                  mappedView.close();
               } catch (Throwable var16) {
                  var20.addSuppressed(var16);
               }
            }

            throw var20;
         }

         if (mappedView != null) {
            mappedView.close();
         }

         gpuBuffer.close();
      };
      AtomicInteger atomicInteger = new AtomicInteger();
      int k = 0;

      for(int l = 0; l <= scales; ++l) {
         commandEncoder.copyTextureToBuffer(texture, gpuBuffer, k, () -> {
            if (atomicInteger.getAndIncrement() == scales) {
               runnable.run();
            }

         }, l);
         k += texture.getFormat().pixelSize() * texture.getWidth(l) * texture.getHeight(l);
      }

   }

   public static Path getDebugTexturePath(Path path) {
      return path.resolve("screenshots").resolve("debug");
   }

   public static Path getDebugTexturePath() {
      return getDebugTexturePath(Path.of("."));
   }
}
