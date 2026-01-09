package net.minecraft.server.network;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Consumer;
import net.minecraft.util.math.ChunkPos;

public interface ChunkFilter {
   ChunkFilter IGNORE_ALL = new ChunkFilter() {
      public boolean isWithinDistance(int x, int z, boolean includeEdge) {
         return false;
      }

      public void forEach(Consumer consumer) {
      }
   };

   static ChunkFilter cylindrical(ChunkPos center, int viewDistance) {
      return new Cylindrical(center, viewDistance);
   }

   static void forEachChangedChunk(ChunkFilter oldFilter, ChunkFilter newFilter, Consumer newlyIncluded, Consumer justRemoved) {
      if (!oldFilter.equals(newFilter)) {
         if (oldFilter instanceof Cylindrical) {
            Cylindrical cylindrical = (Cylindrical)oldFilter;
            if (newFilter instanceof Cylindrical) {
               Cylindrical cylindrical2 = (Cylindrical)newFilter;
               if (cylindrical.overlaps(cylindrical2)) {
                  int i = Math.min(cylindrical.getLeft(), cylindrical2.getLeft());
                  int j = Math.min(cylindrical.getBottom(), cylindrical2.getBottom());
                  int k = Math.max(cylindrical.getRight(), cylindrical2.getRight());
                  int l = Math.max(cylindrical.getTop(), cylindrical2.getTop());

                  for(int m = i; m <= k; ++m) {
                     for(int n = j; n <= l; ++n) {
                        boolean bl = cylindrical.isWithinDistance(m, n);
                        boolean bl2 = cylindrical2.isWithinDistance(m, n);
                        if (bl != bl2) {
                           if (bl2) {
                              newlyIncluded.accept(new ChunkPos(m, n));
                           } else {
                              justRemoved.accept(new ChunkPos(m, n));
                           }
                        }
                     }
                  }

                  return;
               }
            }
         }

         oldFilter.forEach(justRemoved);
         newFilter.forEach(newlyIncluded);
      }
   }

   default boolean isWithinDistance(ChunkPos pos) {
      return this.isWithinDistance(pos.x, pos.z);
   }

   default boolean isWithinDistance(int x, int z) {
      return this.isWithinDistance(x, z, true);
   }

   boolean isWithinDistance(int x, int z, boolean includeEdge);

   void forEach(Consumer consumer);

   default boolean isWithinDistanceExcludingEdge(int x, int z) {
      return this.isWithinDistance(x, z, false);
   }

   static boolean isWithinDistanceExcludingEdge(int centerX, int centerZ, int viewDistance, int x, int z) {
      return isWithinDistance(centerX, centerZ, viewDistance, x, z, false);
   }

   static boolean isWithinDistance(int centerX, int centerZ, int viewDistance, int x, int z, boolean includeEdge) {
      int i = includeEdge ? 2 : 1;
      long l = (long)Math.max(0, Math.abs(x - centerX) - i);
      long m = (long)Math.max(0, Math.abs(z - centerZ) - i);
      long n = l * l + m * m;
      int j = viewDistance * viewDistance;
      return n < (long)j;
   }

   public static record Cylindrical(ChunkPos center, int viewDistance) implements ChunkFilter {
      public Cylindrical(ChunkPos chunkPos, int i) {
         this.center = chunkPos;
         this.viewDistance = i;
      }

      int getLeft() {
         return this.center.x - this.viewDistance - 1;
      }

      int getBottom() {
         return this.center.z - this.viewDistance - 1;
      }

      int getRight() {
         return this.center.x + this.viewDistance + 1;
      }

      int getTop() {
         return this.center.z + this.viewDistance + 1;
      }

      @VisibleForTesting
      protected boolean overlaps(Cylindrical o) {
         return this.getLeft() <= o.getRight() && this.getRight() >= o.getLeft() && this.getBottom() <= o.getTop() && this.getTop() >= o.getBottom();
      }

      public boolean isWithinDistance(int x, int z, boolean includeEdge) {
         return ChunkFilter.isWithinDistance(this.center.x, this.center.z, this.viewDistance, x, z, includeEdge);
      }

      public void forEach(Consumer consumer) {
         for(int i = this.getLeft(); i <= this.getRight(); ++i) {
            for(int j = this.getBottom(); j <= this.getTop(); ++j) {
               if (this.isWithinDistance(i, j)) {
                  consumer.accept(new ChunkPos(i, j));
               }
            }
         }

      }

      public ChunkPos center() {
         return this.center;
      }

      public int viewDistance() {
         return this.viewDistance;
      }
   }
}
