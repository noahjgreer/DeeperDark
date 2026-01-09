package com.mojang.blaze3d.buffers;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public abstract class GpuBuffer implements AutoCloseable {
   public static final int USAGE_MAP_READ = 1;
   public static final int USAGE_MAP_WRITE = 2;
   public static final int USAGE_HINT_CLIENT_STORAGE = 4;
   public static final int USAGE_COPY_DST = 8;
   public static final int USAGE_COPY_SRC = 16;
   public static final int USAGE_VERTEX = 32;
   public static final int USAGE_INDEX = 64;
   public static final int USAGE_UNIFORM = 128;
   public static final int USAGE_UNIFORM_TEXEL_BUFFER = 256;
   private final int usage;
   public int size;

   public GpuBuffer(int usage, int size) {
      this.size = size;
      this.usage = usage;
   }

   public int size() {
      return this.size;
   }

   public int usage() {
      return this.usage;
   }

   public abstract boolean isClosed();

   public abstract void close();

   public GpuBufferSlice slice(int offset, int length) {
      if (offset >= 0 && length >= 0 && offset + length <= this.size) {
         return new GpuBufferSlice(this, offset, length);
      } else {
         throw new IllegalArgumentException("Offset of " + offset + " and length " + length + " would put new slice outside buffer's range (of 0," + length + ")");
      }
   }

   public GpuBufferSlice slice() {
      return new GpuBufferSlice(this, 0, this.size);
   }

   @Environment(EnvType.CLIENT)
   @DeobfuscateClass
   public interface MappedView extends AutoCloseable {
      ByteBuffer data();

      void close();
   }
}
