package net.minecraft.client.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Pool implements ObjectAllocator, AutoCloseable {
   private final int lifespan;
   private final Deque entries = new ArrayDeque();

   public Pool(int lifespan) {
      this.lifespan = lifespan;
   }

   public void decrementLifespan() {
      Iterator iterator = this.entries.iterator();

      while(iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         if (entry.lifespan-- == 0) {
            entry.close();
            iterator.remove();
         }
      }

   }

   public Object acquire(ClosableFactory factory) {
      Object object = this.acquireUnprepared(factory);
      factory.prepare(object);
      return object;
   }

   private Object acquireUnprepared(ClosableFactory factory) {
      Iterator iterator = this.entries.iterator();

      Entry entry;
      do {
         if (!iterator.hasNext()) {
            return factory.create();
         }

         entry = (Entry)iterator.next();
      } while(!factory.equals(entry.factory));

      iterator.remove();
      return entry.object;
   }

   public void release(ClosableFactory factory, Object value) {
      this.entries.addFirst(new Entry(factory, value, this.lifespan));
   }

   public void clear() {
      this.entries.forEach(Entry::close);
      this.entries.clear();
   }

   public void close() {
      this.clear();
   }

   @VisibleForTesting
   protected Collection getEntries() {
      return this.entries;
   }

   @Environment(EnvType.CLIENT)
   @VisibleForTesting
   protected static final class Entry implements AutoCloseable {
      final ClosableFactory factory;
      final Object object;
      int lifespan;

      Entry(ClosableFactory factory, Object object, int lifespan) {
         this.factory = factory;
         this.object = object;
         this.lifespan = lifespan;
      }

      public void close() {
         this.factory.close(this.object);
      }
   }
}
