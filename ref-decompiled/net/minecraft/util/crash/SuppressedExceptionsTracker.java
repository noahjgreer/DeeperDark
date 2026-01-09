package net.minecraft.util.crash;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Queue;
import net.minecraft.util.collection.ArrayListDeque;

public class SuppressedExceptionsTracker {
   private static final int MAX_QUEUE_SIZE = 8;
   private final Queue queue = new ArrayListDeque();
   private final Object2IntLinkedOpenHashMap keyToCount = new Object2IntLinkedOpenHashMap();

   private static long currentTimeMillis() {
      return System.currentTimeMillis();
   }

   public synchronized void onSuppressedException(String location, Throwable exception) {
      long l = currentTimeMillis();
      String string = exception.getMessage();
      this.queue.add(new Entry(l, location, exception.getClass(), string));

      while(this.queue.size() > 8) {
         this.queue.remove();
      }

      Key key = new Key(location, exception.getClass());
      int i = this.keyToCount.getInt(key);
      this.keyToCount.putAndMoveToFirst(key, i + 1);
   }

   public synchronized String collect() {
      long l = currentTimeMillis();
      StringBuilder stringBuilder = new StringBuilder();
      if (!this.queue.isEmpty()) {
         stringBuilder.append("\n\t\tLatest entries:\n");
         Iterator var4 = this.queue.iterator();

         while(var4.hasNext()) {
            Entry entry = (Entry)var4.next();
            stringBuilder.append("\t\t\t").append(entry.location).append(":").append(entry.cls).append(": ").append(entry.message).append(" (").append(l - entry.timestampMs).append("ms ago)").append("\n");
         }
      }

      if (!this.keyToCount.isEmpty()) {
         if (stringBuilder.isEmpty()) {
            stringBuilder.append("\n");
         }

         stringBuilder.append("\t\tEntry counts:\n");
         ObjectIterator var6 = Object2IntMaps.fastIterable(this.keyToCount).iterator();

         while(var6.hasNext()) {
            Object2IntMap.Entry entry2 = (Object2IntMap.Entry)var6.next();
            stringBuilder.append("\t\t\t").append(((Key)entry2.getKey()).location).append(":").append(((Key)entry2.getKey()).cls).append(" x ").append(entry2.getIntValue()).append("\n");
         }
      }

      return stringBuilder.isEmpty() ? "~~NONE~~" : stringBuilder.toString();
   }

   private static record Entry(long timestampMs, String location, Class cls, String message) {
      final long timestampMs;
      final String location;
      final Class cls;
      final String message;

      Entry(long l, String string, Class class_, String string2) {
         this.timestampMs = l;
         this.location = string;
         this.cls = class_;
         this.message = string2;
      }

      public long timestampMs() {
         return this.timestampMs;
      }

      public String location() {
         return this.location;
      }

      public Class cls() {
         return this.cls;
      }

      public String message() {
         return this.message;
      }
   }

   private static record Key(String location, Class cls) {
      final String location;
      final Class cls;

      Key(String string, Class class_) {
         this.location = string;
         this.cls = class_;
      }

      public String location() {
         return this.location;
      }

      public Class cls() {
         return this.cls;
      }
   }
}
