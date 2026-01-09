package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class DynamicUniformStorage implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final List oldBuffers = new ArrayList();
   private final int blockSize;
   private MappableRingBuffer buffer;
   private int size;
   private int capacity;
   @Nullable
   private Uploadable lastWrittenValue;
   private final String name;

   public DynamicUniformStorage(String name, int blockSize, int capacity) {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      this.blockSize = MathHelper.roundUpToMultiple(blockSize, gpuDevice.getUniformOffsetAlignment());
      this.capacity = MathHelper.smallestEncompassingPowerOfTwo(capacity);
      this.size = 0;
      this.buffer = new MappableRingBuffer(() -> {
         return name + " x" + this.blockSize;
      }, 130, this.blockSize * this.capacity);
      this.name = name;
   }

   public void clear() {
      this.size = 0;
      this.lastWrittenValue = null;
      this.buffer.rotate();
      if (!this.oldBuffers.isEmpty()) {
         Iterator var1 = this.oldBuffers.iterator();

         while(var1.hasNext()) {
            MappableRingBuffer mappableRingBuffer = (MappableRingBuffer)var1.next();
            mappableRingBuffer.close();
         }

         this.oldBuffers.clear();
      }

   }

   private void growBuffer(int capacity) {
      this.capacity = capacity;
      this.size = 0;
      this.lastWrittenValue = null;
      this.oldBuffers.add(this.buffer);
      this.buffer = new MappableRingBuffer(() -> {
         return this.name + " x" + this.blockSize;
      }, 130, this.blockSize * this.capacity);
   }

   public GpuBufferSlice write(Uploadable value) {
      if (this.lastWrittenValue != null && this.lastWrittenValue.equals(value)) {
         return this.buffer.getBlocking().slice((this.size - 1) * this.blockSize, this.blockSize);
      } else {
         int i;
         if (this.size >= this.capacity) {
            i = this.capacity * 2;
            LOGGER.info("Resizing " + this.name + ", capacity limit of {} reached during a single frame. New capacity will be {}.", this.capacity, i);
            this.growBuffer(i);
         }

         i = this.size * this.blockSize;
         GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.buffer.getBlocking().slice(i, this.blockSize), false, true);

         try {
            value.write(mappedView.data());
         } catch (Throwable var7) {
            if (mappedView != null) {
               try {
                  mappedView.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (mappedView != null) {
            mappedView.close();
         }

         ++this.size;
         this.lastWrittenValue = value;
         return this.buffer.getBlocking().slice(i, this.blockSize);
      }
   }

   public GpuBufferSlice[] writeAll(Uploadable[] values) {
      if (values.length == 0) {
         return new GpuBufferSlice[0];
      } else {
         int i;
         if (this.size + values.length > this.capacity) {
            i = MathHelper.smallestEncompassingPowerOfTwo(Math.max(this.capacity + 1, values.length));
            LOGGER.info("Resizing " + this.name + ", capacity limit of {} reached during a single frame. New capacity will be {}.", this.capacity, i);
            this.growBuffer(i);
         }

         i = this.size * this.blockSize;
         GpuBufferSlice[] gpuBufferSlices = new GpuBufferSlice[values.length];
         GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.buffer.getBlocking().slice(i, values.length * this.blockSize), false, true);

         try {
            ByteBuffer byteBuffer = mappedView.data();

            for(int j = 0; j < values.length; ++j) {
               Uploadable uploadable = values[j];
               gpuBufferSlices[j] = this.buffer.getBlocking().slice(i + j * this.blockSize, this.blockSize);
               byteBuffer.position(j * this.blockSize);
               uploadable.write(byteBuffer);
            }
         } catch (Throwable var9) {
            if (mappedView != null) {
               try {
                  mappedView.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (mappedView != null) {
            mappedView.close();
         }

         this.size += values.length;
         this.lastWrittenValue = values[values.length - 1];
         return gpuBufferSlices;
      }
   }

   public void close() {
      Iterator var1 = this.oldBuffers.iterator();

      while(var1.hasNext()) {
         MappableRingBuffer mappableRingBuffer = (MappableRingBuffer)var1.next();
         mappableRingBuffer.close();
      }

      this.buffer.close();
   }

   @Environment(EnvType.CLIENT)
   public interface Uploadable {
      void write(ByteBuffer buffer);
   }
}
