package com.mojang.blaze3d.buffers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public record GpuBufferSlice(GpuBuffer buffer, int offset, int length) {
   public GpuBufferSlice(GpuBuffer gpuBuffer, int i, int j) {
      this.buffer = gpuBuffer;
      this.offset = i;
      this.length = j;
   }

   public GpuBufferSlice slice(int offset, int length) {
      if (offset >= 0 && length >= 0 && offset + length < this.length) {
         return new GpuBufferSlice(this.buffer, this.offset + offset, length);
      } else {
         throw new IllegalArgumentException("Offset of " + offset + " and length " + length + " would put new slice outside existing slice's range (of " + offset + "," + length + ")");
      }
   }

   public GpuBuffer buffer() {
      return this.buffer;
   }

   public int offset() {
      return this.offset;
   }

   public int length() {
      return this.length;
   }
}
