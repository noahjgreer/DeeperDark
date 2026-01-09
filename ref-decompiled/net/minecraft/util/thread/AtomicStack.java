package net.minecraft.util.thread;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class AtomicStack {
   private final AtomicReferenceArray contents;
   private final AtomicInteger size;

   public AtomicStack(int maxSize) {
      this.contents = new AtomicReferenceArray(maxSize);
      this.size = new AtomicInteger(0);
   }

   public void push(Object value) {
      int i = this.contents.length();

      int j;
      int k;
      do {
         j = this.size.get();
         k = (j + 1) % i;
      } while(!this.size.compareAndSet(j, k));

      this.contents.set(k, value);
   }

   public List toList() {
      int i = this.size.get();
      ImmutableList.Builder builder = ImmutableList.builder();

      for(int j = 0; j < this.contents.length(); ++j) {
         int k = Math.floorMod(i - j, this.contents.length());
         Object object = this.contents.get(k);
         if (object != null) {
            builder.add(object);
         }
      }

      return builder.build();
   }
}
