package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public abstract class GpuBufferManager {
   public static GpuBufferManager create(GLCapabilities capabilities, Set usedCapabilities) {
      if (capabilities.GL_ARB_buffer_storage && GlBackend.allowGlBufferStorage) {
         usedCapabilities.add("GL_ARB_buffer_storage");
         return new ARBGpuBufferManager();
      } else {
         return new DirectGpuBufferManager();
      }
   }

   public abstract GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier debugLabelSupplier, int usage, int size);

   public abstract GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier debugLabelSupplier, int usage, ByteBuffer data);

   public abstract GlGpuBuffer.Mapped mapBufferRange(BufferManager bufferManager, GlGpuBuffer buffer, int offset, int length, int flags);

   @Environment(EnvType.CLIENT)
   static class ARBGpuBufferManager extends GpuBufferManager {
      public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier debugLabelSupplier, int usage, int size) {
         int i = bufferManager.createBuffer();
         bufferManager.setBufferStorage(i, (long)size, GlConst.bufferUsageToGlFlag(usage));
         ByteBuffer byteBuffer = this.mapBufferRange(bufferManager, usage, i, size);
         return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, size, i, byteBuffer);
      }

      public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier debugLabelSupplier, int usage, ByteBuffer data) {
         int i = bufferManager.createBuffer();
         int j = data.remaining();
         bufferManager.setBufferStorage(i, data, GlConst.bufferUsageToGlFlag(usage));
         ByteBuffer byteBuffer = this.mapBufferRange(bufferManager, usage, i, j);
         return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, j, i, byteBuffer);
      }

      @Nullable
      private ByteBuffer mapBufferRange(BufferManager bufferManager, int usage, int buffer, int length) {
         int i = 0;
         if ((usage & 1) != 0) {
            i |= 1;
         }

         if ((usage & 2) != 0) {
            i |= 18;
         }

         ByteBuffer byteBuffer;
         if (i != 0) {
            GlStateManager.clearGlErrors();
            byteBuffer = bufferManager.mapBufferRange(buffer, 0, length, i | 64);
            if (byteBuffer == null) {
               throw new IllegalStateException("Can't persistently map buffer, opengl error " + GlStateManager._getError());
            }
         } else {
            byteBuffer = null;
         }

         return byteBuffer;
      }

      public GlGpuBuffer.Mapped mapBufferRange(BufferManager bufferManager, GlGpuBuffer buffer, int offset, int length, int flags) {
         if (buffer.backingBuffer == null) {
            throw new IllegalStateException("Somehow trying to map an unmappable buffer");
         } else {
            return new GlGpuBuffer.Mapped(() -> {
               if ((flags & 2) != 0) {
                  bufferManager.flushMappedBufferRange(buffer.id, offset, length);
               }

            }, buffer, MemoryUtil.memSlice(buffer.backingBuffer, offset, length));
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private static class DirectGpuBufferManager extends GpuBufferManager {
      DirectGpuBufferManager() {
      }

      public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier debugLabelSupplier, int usage, int size) {
         int i = bufferManager.createBuffer();
         bufferManager.setBufferData(i, (long)size, GlConst.bufferUsageToGlEnum(usage));
         return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, size, i, (ByteBuffer)null);
      }

      public GlGpuBuffer createBuffer(BufferManager bufferManager, @Nullable Supplier debugLabelSupplier, int usage, ByteBuffer data) {
         int i = bufferManager.createBuffer();
         int j = data.remaining();
         bufferManager.setBufferData(i, data, GlConst.bufferUsageToGlEnum(usage));
         return new GlGpuBuffer(debugLabelSupplier, bufferManager, usage, j, i, (ByteBuffer)null);
      }

      public GlGpuBuffer.Mapped mapBufferRange(BufferManager bufferManager, GlGpuBuffer buffer, int offset, int length, int flags) {
         GlStateManager.clearGlErrors();
         ByteBuffer byteBuffer = bufferManager.mapBufferRange(buffer.id, offset, length, flags);
         if (byteBuffer == null) {
            throw new IllegalStateException("Can't map buffer, opengl error " + GlStateManager._getError());
         } else {
            return new GlGpuBuffer.Mapped(() -> {
               bufferManager.unmapBuffer(buffer.id);
            }, buffer, byteBuffer);
         }
      }
   }
}
