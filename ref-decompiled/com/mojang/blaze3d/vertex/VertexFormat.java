package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11541;
import net.minecraft.util.Util;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public class VertexFormat {
   public static final int UNKNOWN_ELEMENT = -1;
   private static final boolean USE_STAGING_BUFFER_WORKAROUND;
   @Nullable
   private static GpuBuffer UPLOAD_STAGING_BUFFER;
   private final List elements;
   private final List names;
   private final int vertexSize;
   private final int elementsMask;
   private final int[] offsetsByElement = new int[32];
   @Nullable
   private GpuBuffer immediateDrawVertexBuffer;
   @Nullable
   private GpuBuffer immediateDrawIndexBuffer;

   VertexFormat(List elements, List names, IntList offsets, int vertexSize) {
      this.elements = elements;
      this.names = names;
      this.vertexSize = vertexSize;
      this.elementsMask = elements.stream().mapToInt(VertexFormatElement::mask).reduce(0, (a, b) -> {
         return a | b;
      });

      for(int i = 0; i < this.offsetsByElement.length; ++i) {
         VertexFormatElement vertexFormatElement = VertexFormatElement.byId(i);
         int j = vertexFormatElement != null ? elements.indexOf(vertexFormatElement) : -1;
         this.offsetsByElement[i] = j != -1 ? offsets.getInt(j) : -1;
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   public String toString() {
      return "VertexFormat" + String.valueOf(this.names);
   }

   public int getVertexSize() {
      return this.vertexSize;
   }

   public List getElements() {
      return this.elements;
   }

   public List getElementAttributeNames() {
      return this.names;
   }

   public int[] getOffsetsByElement() {
      return this.offsetsByElement;
   }

   public int getOffset(VertexFormatElement element) {
      return this.offsetsByElement[element.id()];
   }

   public boolean contains(VertexFormatElement element) {
      return (this.elementsMask & element.mask()) != 0;
   }

   public int getElementsMask() {
      return this.elementsMask;
   }

   public String getElementName(VertexFormatElement element) {
      int i = this.elements.indexOf(element);
      if (i == -1) {
         throw new IllegalArgumentException(String.valueOf(element) + " is not contained in format");
      } else {
         return (String)this.names.get(i);
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof VertexFormat) {
            VertexFormat vertexFormat = (VertexFormat)o;
            if (this.elementsMask == vertexFormat.elementsMask && this.vertexSize == vertexFormat.vertexSize && this.names.equals(vertexFormat.names) && Arrays.equals(this.offsetsByElement, vertexFormat.offsetsByElement)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.elementsMask * 31 + Arrays.hashCode(this.offsetsByElement);
   }

   private static GpuBuffer uploadToBuffer(@Nullable GpuBuffer gpuBuffer, ByteBuffer byteBuffer, int i, Supplier supplier) {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      if (gpuBuffer == null) {
         gpuBuffer = gpuDevice.createBuffer(supplier, i, byteBuffer);
      } else {
         CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
         if (gpuBuffer.size() < byteBuffer.remaining()) {
            gpuBuffer.close();
            gpuBuffer = gpuDevice.createBuffer(supplier, i, byteBuffer);
         } else {
            commandEncoder.writeToBuffer(gpuBuffer.slice(), byteBuffer);
         }
      }

      return gpuBuffer;
   }

   private GpuBuffer uploadToBufferWithWorkaround(@Nullable GpuBuffer gpuBuffer, ByteBuffer byteBuffer, int i, Supplier supplier) {
      GpuDevice gpuDevice = RenderSystem.getDevice();
      if (USE_STAGING_BUFFER_WORKAROUND) {
         if (gpuBuffer == null) {
            gpuBuffer = gpuDevice.createBuffer(supplier, i, byteBuffer);
         } else {
            CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
            if (gpuBuffer.size() < byteBuffer.remaining()) {
               gpuBuffer.close();
               gpuBuffer = gpuDevice.createBuffer(supplier, i, byteBuffer);
            } else {
               UPLOAD_STAGING_BUFFER = uploadToBuffer(UPLOAD_STAGING_BUFFER, byteBuffer, i, supplier);
               commandEncoder.copyToBuffer(UPLOAD_STAGING_BUFFER.slice(0, byteBuffer.remaining()), gpuBuffer.slice(0, byteBuffer.remaining()));
            }
         }

         return gpuBuffer;
      } else if (class_11541.method_72243(gpuDevice).method_72242()) {
         if (gpuBuffer != null) {
            gpuBuffer.close();
         }

         return gpuDevice.createBuffer(supplier, i, byteBuffer);
      } else {
         return uploadToBuffer(gpuBuffer, byteBuffer, i, supplier);
      }
   }

   public GpuBuffer uploadImmediateVertexBuffer(ByteBuffer byteBuffer) {
      this.immediateDrawVertexBuffer = this.uploadToBufferWithWorkaround(this.immediateDrawVertexBuffer, byteBuffer, 40, () -> {
         return "Immediate vertex buffer for " + String.valueOf(this);
      });
      return this.immediateDrawVertexBuffer;
   }

   public GpuBuffer uploadImmediateIndexBuffer(ByteBuffer byteBuffer) {
      this.immediateDrawIndexBuffer = this.uploadToBufferWithWorkaround(this.immediateDrawIndexBuffer, byteBuffer, 72, () -> {
         return "Immediate index buffer for " + String.valueOf(this);
      });
      return this.immediateDrawIndexBuffer;
   }

   static {
      USE_STAGING_BUFFER_WORKAROUND = Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS && Util.isOnAarch64();
   }

   @Environment(EnvType.CLIENT)
   @DeobfuscateClass
   public static class Builder {
      private final ImmutableMap.Builder elements = ImmutableMap.builder();
      private final IntList offsets = new IntArrayList();
      private int offset;

      Builder() {
      }

      public Builder add(String name, VertexFormatElement element) {
         this.elements.put(name, element);
         this.offsets.add(this.offset);
         this.offset += element.byteSize();
         return this;
      }

      public Builder padding(int padding) {
         this.offset += padding;
         return this;
      }

      public VertexFormat build() {
         ImmutableMap immutableMap = this.elements.buildOrThrow();
         ImmutableList immutableList = immutableMap.values().asList();
         ImmutableList immutableList2 = immutableMap.keySet().asList();
         return new VertexFormat(immutableList, immutableList2, this.offsets, this.offset);
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum DrawMode {
      LINES(2, 2, false),
      LINE_STRIP(2, 1, true),
      DEBUG_LINES(2, 2, false),
      DEBUG_LINE_STRIP(2, 1, true),
      TRIANGLES(3, 3, false),
      TRIANGLE_STRIP(3, 1, true),
      TRIANGLE_FAN(3, 1, true),
      QUADS(4, 4, false);

      public final int firstVertexCount;
      public final int additionalVertexCount;
      public final boolean shareVertices;

      private DrawMode(final int firstVertexCount, final int additionalVertexCount, final boolean shareVertices) {
         this.firstVertexCount = firstVertexCount;
         this.additionalVertexCount = additionalVertexCount;
         this.shareVertices = shareVertices;
      }

      public int getIndexCount(int vertexCount) {
         int i;
         switch (this.ordinal()) {
            case 0:
            case 7:
               i = vertexCount / 4 * 6;
               break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
               i = vertexCount;
               break;
            default:
               i = 0;
         }

         return i;
      }

      // $FF: synthetic method
      private static DrawMode[] method_36817() {
         return new DrawMode[]{LINES, LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum IndexType {
      SHORT(2),
      INT(4);

      public final int size;

      private IndexType(final int size) {
         this.size = size;
      }

      public static IndexType smallestFor(int i) {
         return (i & -65536) != 0 ? INT : SHORT;
      }

      // $FF: synthetic method
      private static IndexType[] method_36816() {
         return new IndexType[]{SHORT, INT};
      }
   }
}
