package net.minecraft.util.collection;

import java.util.Locale;
import java.util.function.Consumer;

public class BoundedRegionArray {
   private final int minX;
   private final int minZ;
   private final int maxX;
   private final int maxZ;
   private final Object[] array;

   public static BoundedRegionArray create(int centerX, int centerZ, int radius, Getter getter) {
      int i = centerX - radius;
      int j = centerZ - radius;
      int k = 2 * radius + 1;
      return new BoundedRegionArray(i, j, k, k, getter);
   }

   private BoundedRegionArray(int minX, int minZ, int maxX, int maxZ, Getter getter) {
      this.minX = minX;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxZ = maxZ;
      this.array = new Object[this.maxX * this.maxZ];

      for(int i = minX; i < minX + maxX; ++i) {
         for(int j = minZ; j < minZ + maxZ; ++j) {
            this.array[this.toIndex(i, j)] = getter.get(i, j);
         }
      }

   }

   public void forEach(Consumer callback) {
      Object[] var2 = this.array;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object object = var2[var4];
         callback.accept(object);
      }

   }

   public Object get(int x, int z) {
      if (!this.isWithinBounds(x, z)) {
         throw new IllegalArgumentException("Requested out of range value (" + x + "," + z + ") from " + String.valueOf(this));
      } else {
         return this.array[this.toIndex(x, z)];
      }
   }

   public boolean isWithinBounds(int x, int z) {
      int i = x - this.minX;
      int j = z - this.minZ;
      return i >= 0 && i < this.maxX && j >= 0 && j < this.maxZ;
   }

   public String toString() {
      return String.format(Locale.ROOT, "StaticCache2D[%d, %d, %d, %d]", this.minX, this.minZ, this.minX + this.maxX, this.minZ + this.maxZ);
   }

   private int toIndex(int x, int z) {
      int i = x - this.minX;
      int j = z - this.minZ;
      return i * this.maxZ + j;
   }

   @FunctionalInterface
   public interface Getter {
      Object get(int x, int z);
   }
}
