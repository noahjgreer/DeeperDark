package net.minecraft.client.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BiomeColorCache {
   private static final int MAX_ENTRY_SIZE = 256;
   private final ThreadLocal last = ThreadLocal.withInitial(Last::new);
   private final Long2ObjectLinkedOpenHashMap colors = new Long2ObjectLinkedOpenHashMap(256, 0.25F);
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private final ToIntFunction colorFactory;

   public BiomeColorCache(ToIntFunction colorFactory) {
      this.colorFactory = colorFactory;
   }

   public int getBiomeColor(BlockPos pos) {
      int i = ChunkSectionPos.getSectionCoord(pos.getX());
      int j = ChunkSectionPos.getSectionCoord(pos.getZ());
      Last last = (Last)this.last.get();
      if (last.x != i || last.z != j || last.colors == null || last.colors.needsCacheRefresh()) {
         last.x = i;
         last.z = j;
         last.colors = this.getColorArray(i, j);
      }

      int[] is = last.colors.get(pos.getY());
      int k = pos.getX() & 15;
      int l = pos.getZ() & 15;
      int m = l << 4 | k;
      int n = is[m];
      if (n != -1) {
         return n;
      } else {
         int o = this.colorFactory.applyAsInt(pos);
         is[m] = o;
         return o;
      }
   }

   public void reset(int chunkX, int chunkZ) {
      try {
         this.lock.writeLock().lock();

         for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
               long l = ChunkPos.toLong(chunkX + i, chunkZ + j);
               Colors colors = (Colors)this.colors.remove(l);
               if (colors != null) {
                  colors.setNeedsCacheRefresh();
               }
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public void reset() {
      try {
         this.lock.writeLock().lock();
         this.colors.values().forEach(Colors::setNeedsCacheRefresh);
         this.colors.clear();
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   private Colors getColorArray(int chunkX, int chunkZ) {
      long l = ChunkPos.toLong(chunkX, chunkZ);
      this.lock.readLock().lock();

      Colors colors;
      Colors colors2;
      try {
         colors = (Colors)this.colors.get(l);
         if (colors != null) {
            colors2 = colors;
            return colors2;
         }
      } finally {
         this.lock.readLock().unlock();
      }

      this.lock.writeLock().lock();

      try {
         colors = (Colors)this.colors.get(l);
         if (colors == null) {
            colors2 = new Colors();
            Colors colors3;
            if (this.colors.size() >= 256) {
               colors3 = (Colors)this.colors.removeFirst();
               if (colors3 != null) {
                  colors3.setNeedsCacheRefresh();
               }
            }

            this.colors.put(l, colors2);
            colors3 = colors2;
            return colors3;
         }

         colors2 = colors;
      } finally {
         this.lock.writeLock().unlock();
      }

      return colors2;
   }

   @Environment(EnvType.CLIENT)
   static class Last {
      public int x = Integer.MIN_VALUE;
      public int z = Integer.MIN_VALUE;
      @Nullable
      Colors colors;

      private Last() {
      }
   }

   @Environment(EnvType.CLIENT)
   static class Colors {
      private final Int2ObjectArrayMap colors = new Int2ObjectArrayMap(16);
      private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
      private static final int XZ_COLORS_SIZE = MathHelper.square(16);
      private volatile boolean needsCacheRefresh;

      public int[] get(int y) {
         this.lock.readLock().lock();

         int[] is;
         try {
            is = (int[])this.colors.get(y);
            if (is != null) {
               int[] var3 = is;
               return var3;
            }
         } finally {
            this.lock.readLock().unlock();
         }

         this.lock.writeLock().lock();

         try {
            is = (int[])this.colors.computeIfAbsent(y, (yx) -> {
               return this.createDefault();
            });
         } finally {
            this.lock.writeLock().unlock();
         }

         return is;
      }

      private int[] createDefault() {
         int[] is = new int[XZ_COLORS_SIZE];
         Arrays.fill(is, -1);
         return is;
      }

      public boolean needsCacheRefresh() {
         return this.needsCacheRefresh;
      }

      public void setNeedsCacheRefresh() {
         this.needsCacheRefresh = true;
      }
   }
}
