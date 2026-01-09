package net.minecraft.util.collection;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class IdList implements IndexedIterable {
   private int nextId;
   private final Reference2IntMap idMap;
   private final List list;

   public IdList() {
      this(512);
   }

   public IdList(int initialSize) {
      this.list = Lists.newArrayListWithExpectedSize(initialSize);
      this.idMap = new Reference2IntOpenHashMap(initialSize);
      this.idMap.defaultReturnValue(-1);
   }

   public void set(Object value, int id) {
      this.idMap.put(value, id);

      while(this.list.size() <= id) {
         this.list.add((Object)null);
      }

      this.list.set(id, value);
      if (this.nextId <= id) {
         this.nextId = id + 1;
      }

   }

   public void add(Object value) {
      this.set(value, this.nextId);
   }

   public int getRawId(Object value) {
      return this.idMap.getInt(value);
   }

   @Nullable
   public final Object get(int index) {
      return index >= 0 && index < this.list.size() ? this.list.get(index) : null;
   }

   public Iterator iterator() {
      return Iterators.filter(this.list.iterator(), Objects::nonNull);
   }

   public boolean containsKey(int index) {
      return this.get(index) != null;
   }

   public int size() {
      return this.idMap.size();
   }
}
