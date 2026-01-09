package net.minecraft.client.render.chunk;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ChunkRenderTaskScheduler {
   private static final int field_53953 = 2;
   private int remainingPrioritizableTasks = 2;
   private final List queue = new ObjectArrayList();
   private volatile int size = 0;

   public synchronized void enqueue(ChunkBuilder.BuiltChunk.Task task) {
      this.queue.add(task);
      ++this.size;
   }

   @Nullable
   public synchronized ChunkBuilder.BuiltChunk.Task dequeueNearest(Vec3d pos) {
      int i = -1;
      int j = -1;
      double d = Double.MAX_VALUE;
      double e = Double.MAX_VALUE;
      ListIterator listIterator = this.queue.listIterator();

      while(listIterator.hasNext()) {
         int k = listIterator.nextIndex();
         ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)listIterator.next();
         if (task.cancelled.get()) {
            listIterator.remove();
         } else {
            double f = task.getOrigin().getSquaredDistance(pos);
            if (!task.isPrioritized() && f < d) {
               d = f;
               i = k;
            }

            if (task.isPrioritized() && f < e) {
               e = f;
               j = k;
            }
         }
      }

      boolean bl = j >= 0;
      boolean bl2 = i >= 0;
      if (!bl || bl2 && (this.remainingPrioritizableTasks <= 0 || !(e < d))) {
         this.remainingPrioritizableTasks = 2;
         return this.remove(i);
      } else {
         --this.remainingPrioritizableTasks;
         return this.remove(j);
      }
   }

   public int size() {
      return this.size;
   }

   @Nullable
   private ChunkBuilder.BuiltChunk.Task remove(int index) {
      if (index >= 0) {
         --this.size;
         return (ChunkBuilder.BuiltChunk.Task)this.queue.remove(index);
      } else {
         return null;
      }
   }

   public synchronized void cancelAll() {
      Iterator var1 = this.queue.iterator();

      while(var1.hasNext()) {
         ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)var1.next();
         task.cancel();
      }

      this.queue.clear();
      this.size = 0;
   }
}
