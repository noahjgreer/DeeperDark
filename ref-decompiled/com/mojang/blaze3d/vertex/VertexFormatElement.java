package com.mojang.blaze3d.vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
@DeobfuscateClass
public record VertexFormatElement(int id, int index, Type type, Usage usage, int count) {
   public static final int MAX_COUNT = 32;
   private static final VertexFormatElement[] BY_ID = new VertexFormatElement[32];
   private static final List ELEMENTS = new ArrayList(32);
   public static final VertexFormatElement POSITION;
   public static final VertexFormatElement COLOR;
   public static final VertexFormatElement UV0;
   public static final VertexFormatElement UV;
   public static final VertexFormatElement UV1;
   public static final VertexFormatElement UV2;
   public static final VertexFormatElement NORMAL;

   public VertexFormatElement(int i, int j, Type type, Usage usage, int k) {
      if (i >= 0 && i < BY_ID.length) {
         if (!this.supportsUsage(j, usage)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
         } else {
            this.id = i;
            this.index = j;
            this.type = type;
            this.usage = usage;
            this.count = k;
         }
      } else {
         throw new IllegalArgumentException("Element ID must be in range [0; " + BY_ID.length + ")");
      }
   }

   public static VertexFormatElement register(int id, int index, Type type, Usage usage, int count) {
      VertexFormatElement vertexFormatElement = new VertexFormatElement(id, index, type, usage, count);
      if (BY_ID[id] != null) {
         throw new IllegalArgumentException("Duplicate element registration for: " + id);
      } else {
         BY_ID[id] = vertexFormatElement;
         ELEMENTS.add(vertexFormatElement);
         return vertexFormatElement;
      }
   }

   private boolean supportsUsage(int uvIndex, Usage usage) {
      return uvIndex == 0 || usage == VertexFormatElement.Usage.UV;
   }

   public String toString() {
      int var10000 = this.count;
      return "" + var10000 + "," + String.valueOf(this.usage) + "," + String.valueOf(this.type) + " (" + this.id + ")";
   }

   public int mask() {
      return 1 << this.id;
   }

   public int byteSize() {
      return this.type.size() * this.count;
   }

   @Nullable
   public static VertexFormatElement byId(int id) {
      return BY_ID[id];
   }

   public static Stream elementsFromMask(int mask) {
      return ELEMENTS.stream().filter((element) -> {
         return element != null && (mask & element.mask()) != 0;
      });
   }

   public int id() {
      return this.id;
   }

   public int index() {
      return this.index;
   }

   public Type type() {
      return this.type;
   }

   public Usage usage() {
      return this.usage;
   }

   public int count() {
      return this.count;
   }

   static {
      POSITION = register(0, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
      COLOR = register(1, 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
      UV0 = register(2, 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      UV = UV0;
      UV1 = register(3, 1, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      UV2 = register(4, 2, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.UV, 2);
      NORMAL = register(5, 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
   }

   @Environment(EnvType.CLIENT)
   @DeobfuscateClass
   public static enum Type {
      FLOAT(4, "Float"),
      UBYTE(1, "Unsigned Byte"),
      BYTE(1, "Byte"),
      USHORT(2, "Unsigned Short"),
      SHORT(2, "Short"),
      UINT(4, "Unsigned Int"),
      INT(4, "Int");

      private final int size;
      private final String name;

      private Type(final int size, final String name) {
         this.size = size;
         this.name = name;
      }

      public int size() {
         return this.size;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{FLOAT, UBYTE, BYTE, USHORT, SHORT, UINT, INT};
      }
   }

   @Environment(EnvType.CLIENT)
   @DeobfuscateClass
   public static enum Usage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      GENERIC("Generic");

      private final String name;

      private Usage(final String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Usage[] $values() {
         return new Usage[]{POSITION, NORMAL, COLOR, UV, GENERIC};
      }
   }
}
