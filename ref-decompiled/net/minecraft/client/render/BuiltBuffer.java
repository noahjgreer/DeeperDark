package net.minecraft.client.render;

import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.BufferAllocator;
import org.apache.commons.lang3.mutable.MutableLong;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class BuiltBuffer implements AutoCloseable {
   private final BufferAllocator.CloseableBuffer buffer;
   @Nullable
   private BufferAllocator.CloseableBuffer sortedBuffer;
   private final DrawParameters drawParameters;

   public BuiltBuffer(BufferAllocator.CloseableBuffer buffer, DrawParameters drawParameters) {
      this.buffer = buffer;
      this.drawParameters = drawParameters;
   }

   private static Vector3f[] collectCentroids(ByteBuffer buffer, int vertexCount, VertexFormat format) {
      int i = format.getOffset(VertexFormatElement.POSITION);
      if (i == -1) {
         throw new IllegalArgumentException("Cannot identify quad centers with no position element");
      } else {
         FloatBuffer floatBuffer = buffer.asFloatBuffer();
         int j = format.getVertexSize() / 4;
         int k = j * 4;
         int l = vertexCount / 4;
         Vector3f[] vector3fs = new Vector3f[l];

         for(int m = 0; m < l; ++m) {
            int n = m * k + i;
            int o = n + j * 2;
            float f = floatBuffer.get(n + 0);
            float g = floatBuffer.get(n + 1);
            float h = floatBuffer.get(n + 2);
            float p = floatBuffer.get(o + 0);
            float q = floatBuffer.get(o + 1);
            float r = floatBuffer.get(o + 2);
            vector3fs[m] = new Vector3f((f + p) / 2.0F, (g + q) / 2.0F, (h + r) / 2.0F);
         }

         return vector3fs;
      }
   }

   public ByteBuffer getBuffer() {
      return this.buffer.getBuffer();
   }

   @Nullable
   public ByteBuffer getSortedBuffer() {
      return this.sortedBuffer != null ? this.sortedBuffer.getBuffer() : null;
   }

   public DrawParameters getDrawParameters() {
      return this.drawParameters;
   }

   @Nullable
   public SortState sortQuads(BufferAllocator allocator, VertexSorter sorter) {
      if (this.drawParameters.mode() != VertexFormat.DrawMode.QUADS) {
         return null;
      } else {
         Vector3f[] vector3fs = collectCentroids(this.buffer.getBuffer(), this.drawParameters.vertexCount(), this.drawParameters.format());
         SortState sortState = new SortState(vector3fs, this.drawParameters.indexType());
         this.sortedBuffer = sortState.sortAndStore(allocator, sorter);
         return sortState;
      }
   }

   public void close() {
      this.buffer.close();
      if (this.sortedBuffer != null) {
         this.sortedBuffer.close();
      }

   }

   @Environment(EnvType.CLIENT)
   public static record DrawParameters(VertexFormat format, int vertexCount, int indexCount, VertexFormat.DrawMode mode, VertexFormat.IndexType indexType) {
      public DrawParameters(VertexFormat vertexFormat, int i, int j, VertexFormat.DrawMode drawMode, VertexFormat.IndexType indexType) {
         this.format = vertexFormat;
         this.vertexCount = i;
         this.indexCount = j;
         this.mode = drawMode;
         this.indexType = indexType;
      }

      public VertexFormat format() {
         return this.format;
      }

      public int vertexCount() {
         return this.vertexCount;
      }

      public int indexCount() {
         return this.indexCount;
      }

      public VertexFormat.DrawMode mode() {
         return this.mode;
      }

      public VertexFormat.IndexType indexType() {
         return this.indexType;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record SortState(Vector3f[] centroids, VertexFormat.IndexType indexType) {
      public SortState(Vector3f[] vector3fs, VertexFormat.IndexType indexType) {
         this.centroids = vector3fs;
         this.indexType = indexType;
      }

      @Nullable
      public BufferAllocator.CloseableBuffer sortAndStore(BufferAllocator allocator, VertexSorter sorter) {
         int[] is = sorter.sort(this.centroids);
         long l = allocator.allocate(is.length * 6 * this.indexType.size);
         IntConsumer intConsumer = this.getStorer(l, this.indexType);
         int[] var7 = is;
         int var8 = is.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            int i = var7[var9];
            intConsumer.accept(i * 4 + 0);
            intConsumer.accept(i * 4 + 1);
            intConsumer.accept(i * 4 + 2);
            intConsumer.accept(i * 4 + 2);
            intConsumer.accept(i * 4 + 3);
            intConsumer.accept(i * 4 + 0);
         }

         return allocator.getAllocated();
      }

      private IntConsumer getStorer(long pointer, VertexFormat.IndexType indexType) {
         MutableLong mutableLong = new MutableLong(pointer);
         IntConsumer var10000;
         switch (indexType) {
            case SHORT:
               var10000 = (i) -> {
                  MemoryUtil.memPutShort(mutableLong.getAndAdd(2L), (short)i);
               };
               break;
            case INT:
               var10000 = (i) -> {
                  MemoryUtil.memPutInt(mutableLong.getAndAdd(4L), i);
               };
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public Vector3f[] centroids() {
         return this.centroids;
      }

      public VertexFormat.IndexType indexType() {
         return this.indexType;
      }
   }
}
