package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.jtracy.MemoryPool;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GlGpuBuffer extends GpuBuffer {
   protected static final MemoryPool POOL = TracyClient.createMemoryPool("GPU Buffers");
   protected boolean closed;
   @Nullable
   protected final Supplier debugLabelSupplier;
   private final BufferManager bufferManager;
   protected final int id;
   @Nullable
   protected ByteBuffer backingBuffer;

   protected GlGpuBuffer(@Nullable Supplier debugLabelSupplier, BufferManager bufferManager, int usage, int size, int id, @Nullable ByteBuffer backingBuffer) {
      super(usage, size);
      this.debugLabelSupplier = debugLabelSupplier;
      this.bufferManager = bufferManager;
      this.id = id;
      this.backingBuffer = backingBuffer;
      POOL.malloc((long)id, size);
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.backingBuffer != null) {
            this.bufferManager.unmapBuffer(this.id);
            this.backingBuffer = null;
         }

         GlStateManager._glDeleteBuffers(this.id);
         POOL.free((long)this.id);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Mapped implements GpuBuffer.MappedView {
      private final Runnable closer;
      private final GlGpuBuffer backingBuffer;
      private final ByteBuffer data;
      private boolean closed;

      protected Mapped(Runnable closer, GlGpuBuffer backingBuffer, ByteBuffer data) {
         this.closer = closer;
         this.backingBuffer = backingBuffer;
         this.data = data;
      }

      public ByteBuffer data() {
         return this.data;
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            this.closer.run();
         }
      }
   }
}
