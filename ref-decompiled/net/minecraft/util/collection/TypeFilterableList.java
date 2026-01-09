package net.minecraft.util.collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.Util;

public class TypeFilterableList extends AbstractCollection {
   private final Map elementsByType = Maps.newHashMap();
   private final Class elementType;
   private final List allElements = Lists.newArrayList();

   public TypeFilterableList(Class elementType) {
      this.elementType = elementType;
      this.elementsByType.put(elementType, this.allElements);
   }

   public boolean add(Object e) {
      boolean bl = false;
      Iterator var3 = this.elementsByType.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         if (((Class)entry.getKey()).isInstance(e)) {
            bl |= ((List)entry.getValue()).add(e);
         }
      }

      return bl;
   }

   public boolean remove(Object o) {
      boolean bl = false;
      Iterator var3 = this.elementsByType.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         if (((Class)entry.getKey()).isInstance(o)) {
            List list = (List)entry.getValue();
            bl |= list.remove(o);
         }
      }

      return bl;
   }

   public boolean contains(Object o) {
      return this.getAllOfType(o.getClass()).contains(o);
   }

   public Collection getAllOfType(Class type) {
      if (!this.elementType.isAssignableFrom(type)) {
         throw new IllegalArgumentException("Don't know how to search for " + String.valueOf(type));
      } else {
         List list = (List)this.elementsByType.computeIfAbsent(type, (typeClass) -> {
            Stream var10000 = this.allElements.stream();
            Objects.requireNonNull(typeClass);
            return (List)var10000.filter(typeClass::isInstance).collect(Util.toArrayList());
         });
         return Collections.unmodifiableCollection(list);
      }
   }

   public Iterator iterator() {
      return (Iterator)(this.allElements.isEmpty() ? Collections.emptyIterator() : Iterators.unmodifiableIterator(this.allElements.iterator()));
   }

   public List copy() {
      return ImmutableList.copyOf(this.allElements);
   }

   public int size() {
      return this.allElements.size();
   }
}
