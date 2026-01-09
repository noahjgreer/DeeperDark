package net.minecraft.util.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class AsyncHelper {
   private static final int MAX_TASKS = 16;

   public static CompletableFuture mapValues(Map futures, BiFunction function, int batchSize, Executor executor) {
      int i = futures.size();
      if (i == 0) {
         return CompletableFuture.completedFuture(Map.of());
      } else if (i == 1) {
         Map.Entry entry = (Map.Entry)futures.entrySet().iterator().next();
         Object object = entry.getKey();
         Object object2 = entry.getValue();
         return CompletableFuture.supplyAsync(() -> {
            Object object3 = function.apply(object, object2);
            return object3 != null ? Map.of(object, object3) : Map.of();
         }, executor);
      } else {
         Batcher batcher = i <= batchSize ? new Single(function, i) : new Batch(function, i, batchSize);
         return ((Batcher)batcher).mapAsync(futures, executor);
      }
   }

   public static CompletableFuture mapValues(Map futures, BiFunction function, Executor executor) {
      int i = Util.getAvailableBackgroundThreads() * 16;
      return mapValues(futures, function, i, executor);
   }

   static class Single extends Batcher {
      Single(BiFunction function, int size) {
         super(function, size, size);
      }

      protected int getLastIndex(int batch) {
         return 1;
      }

      protected CompletableFuture newBatch(Future futures, int size, int maxCount, Executor executor) {
         assert size + 1 == maxCount;

         return CompletableFuture.runAsync(() -> {
            futures.apply(size);
         }, executor);
      }

      protected CompletableFuture addLastTask(CompletableFuture future, Future entry) {
         return future.thenApply((obj) -> {
            Map map = new HashMap(entry.keySize());

            for(int i = 0; i < entry.keySize(); ++i) {
               entry.copy(i, map);
            }

            return map;
         });
      }
   }

   static class Batch extends Batcher {
      private final Map entries;
      private final int size;
      private final int start;

      Batch(BiFunction biFunction, int i, int j) {
         super(biFunction, i, j);
         this.entries = new HashMap(i);
         this.size = MathHelper.ceilDiv(i, j);
         int k = this.size * j;
         int l = k - i;
         this.start = j - l;

         assert this.start > 0 && this.start <= j;
      }

      protected CompletableFuture newBatch(Future futures, int size, int maxCount, Executor executor) {
         int i = maxCount - size;

         assert i == this.size || i == this.size - 1;

         return CompletableFuture.runAsync(newTask(this.entries, size, maxCount, futures), executor);
      }

      protected int getLastIndex(int batch) {
         return batch < this.start ? this.size : this.size - 1;
      }

      private static Runnable newTask(Map futures, int size, int maxCount, Future entry) {
         return () -> {
            for(int k = size; k < maxCount; ++k) {
               entry.apply(k);
            }

            synchronized(futures) {
               for(int l = size; l < maxCount; ++l) {
                  entry.copy(l, futures);
               }

            }
         };
      }

      protected CompletableFuture addLastTask(CompletableFuture future, Future entry) {
         Map map = this.entries;
         return future.thenApply((obj) -> {
            return map;
         });
      }
   }

   abstract static class Batcher {
      private int lastBatch;
      private int index;
      private final CompletableFuture[] futures;
      private int batch;
      private final Future entry;

      Batcher(BiFunction function, int size, int startAt) {
         this.entry = new Future(function, size);
         this.futures = new CompletableFuture[startAt];
      }

      private int nextSize() {
         return this.index - this.lastBatch;
      }

      public CompletableFuture mapAsync(Map future, Executor executor) {
         future.forEach((key, value) -> {
            this.entry.put(this.index++, key, value);
            if (this.nextSize() == this.getLastIndex(this.batch)) {
               this.futures[this.batch++] = this.newBatch(this.entry, this.lastBatch, this.index, executor);
               this.lastBatch = this.index;
            }

         });

         assert this.index == this.entry.keySize();

         assert this.lastBatch == this.index;

         assert this.batch == this.futures.length;

         return this.addLastTask(CompletableFuture.allOf(this.futures), this.entry);
      }

      protected abstract int getLastIndex(int batch);

      protected abstract CompletableFuture newBatch(Future futures, int size, int maxCount, Executor executor);

      protected abstract CompletableFuture addLastTask(CompletableFuture future, Future entry);
   }

   private static record Future(BiFunction operation, Object[] keys, Object[] values) {
      public Future(BiFunction function, int size) {
         this(function, new Object[size], new Object[size]);
      }

      private Future(BiFunction biFunction, Object[] objects, Object[] objects2) {
         this.operation = biFunction;
         this.keys = objects;
         this.values = objects2;
      }

      public void put(int index, Object key, Object value) {
         this.keys[index] = key;
         this.values[index] = value;
      }

      @Nullable
      private Object getKey(int index) {
         return this.keys[index];
      }

      @Nullable
      private Object getValue(int index) {
         return this.values[index];
      }

      @Nullable
      private Object getUValue(int index) {
         return this.values[index];
      }

      public void apply(int index) {
         this.values[index] = this.operation.apply(this.getKey(index), this.getUValue(index));
      }

      public void copy(int index, Map futures) {
         Object object = this.getValue(index);
         if (object != null) {
            Object object2 = this.getKey(index);
            futures.put(object2, object);
         }

      }

      public int keySize() {
         return this.keys.length;
      }

      public BiFunction operation() {
         return this.operation;
      }

      public Object[] keys() {
         return this.keys;
      }

      public Object[] values() {
         return this.values;
      }
   }
}
