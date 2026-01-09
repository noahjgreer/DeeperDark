package net.minecraft.component;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public final class MergedComponentMap implements ComponentMap {
   private final ComponentMap baseComponents;
   private Reference2ObjectMap changedComponents;
   private boolean copyOnWrite;

   public MergedComponentMap(ComponentMap baseComponents) {
      this(baseComponents, Reference2ObjectMaps.emptyMap(), true);
   }

   private MergedComponentMap(ComponentMap baseComponents, Reference2ObjectMap changedComponents, boolean copyOnWrite) {
      this.baseComponents = baseComponents;
      this.changedComponents = changedComponents;
      this.copyOnWrite = copyOnWrite;
   }

   public static MergedComponentMap create(ComponentMap baseComponents, ComponentChanges changes) {
      if (shouldReuseChangesMap(baseComponents, changes.changedComponents)) {
         return new MergedComponentMap(baseComponents, changes.changedComponents, true);
      } else {
         MergedComponentMap mergedComponentMap = new MergedComponentMap(baseComponents);
         mergedComponentMap.applyChanges(changes);
         return mergedComponentMap;
      }
   }

   private static boolean shouldReuseChangesMap(ComponentMap baseComponents, Reference2ObjectMap changedComponents) {
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(changedComponents).iterator();

      Object object;
      Optional optional;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         Map.Entry entry = (Map.Entry)var2.next();
         object = baseComponents.get((ComponentType)entry.getKey());
         optional = (Optional)entry.getValue();
         if (optional.isPresent() && optional.get().equals(object)) {
            return false;
         }
      } while(!optional.isEmpty() || object != null);

      return false;
   }

   @Nullable
   public Object get(ComponentType type) {
      Optional optional = (Optional)this.changedComponents.get(type);
      return optional != null ? optional.orElse((Object)null) : this.baseComponents.get(type);
   }

   public boolean hasChanged(ComponentType type) {
      return this.changedComponents.containsKey(type);
   }

   @Nullable
   public Object set(ComponentType type, @Nullable Object value) {
      this.onWrite();
      Object object = this.baseComponents.get(type);
      Optional optional;
      if (Objects.equals(value, object)) {
         optional = (Optional)this.changedComponents.remove(type);
      } else {
         optional = (Optional)this.changedComponents.put(type, Optional.ofNullable(value));
      }

      return optional != null ? optional.orElse(object) : object;
   }

   @Nullable
   public Object remove(ComponentType type) {
      this.onWrite();
      Object object = this.baseComponents.get(type);
      Optional optional;
      if (object != null) {
         optional = (Optional)this.changedComponents.put(type, Optional.empty());
      } else {
         optional = (Optional)this.changedComponents.remove(type);
      }

      return optional != null ? optional.orElse((Object)null) : object;
   }

   public void applyChanges(ComponentChanges changes) {
      this.onWrite();
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(changes.changedComponents).iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         this.applyChange((ComponentType)entry.getKey(), (Optional)entry.getValue());
      }

   }

   private void applyChange(ComponentType type, Optional optional) {
      Object object = this.baseComponents.get(type);
      if (optional.isPresent()) {
         if (optional.get().equals(object)) {
            this.changedComponents.remove(type);
         } else {
            this.changedComponents.put(type, optional);
         }
      } else if (object != null) {
         this.changedComponents.put(type, Optional.empty());
      } else {
         this.changedComponents.remove(type);
      }

   }

   public void setChanges(ComponentChanges changes) {
      this.onWrite();
      this.changedComponents.clear();
      this.changedComponents.putAll(changes.changedComponents);
   }

   public void clearChanges() {
      this.onWrite();
      this.changedComponents.clear();
   }

   public void setAll(ComponentMap components) {
      Iterator var2 = components.iterator();

      while(var2.hasNext()) {
         Component component = (Component)var2.next();
         component.apply(this);
      }

   }

   private void onWrite() {
      if (this.copyOnWrite) {
         this.changedComponents = new Reference2ObjectArrayMap(this.changedComponents);
         this.copyOnWrite = false;
      }

   }

   public Set getTypes() {
      if (this.changedComponents.isEmpty()) {
         return this.baseComponents.getTypes();
      } else {
         Set set = new ReferenceArraySet(this.baseComponents.getTypes());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.changedComponents).iterator();

         while(var2.hasNext()) {
            Reference2ObjectMap.Entry entry = (Reference2ObjectMap.Entry)var2.next();
            Optional optional = (Optional)entry.getValue();
            if (optional.isPresent()) {
               set.add((ComponentType)entry.getKey());
            } else {
               set.remove(entry.getKey());
            }
         }

         return set;
      }
   }

   public Iterator iterator() {
      if (this.changedComponents.isEmpty()) {
         return this.baseComponents.iterator();
      } else {
         List list = new ArrayList(this.changedComponents.size() + this.baseComponents.size());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.changedComponents).iterator();

         while(var2.hasNext()) {
            Reference2ObjectMap.Entry entry = (Reference2ObjectMap.Entry)var2.next();
            if (((Optional)entry.getValue()).isPresent()) {
               list.add(Component.of((ComponentType)entry.getKey(), ((Optional)entry.getValue()).get()));
            }
         }

         Iterator var4 = this.baseComponents.iterator();

         while(var4.hasNext()) {
            Component component = (Component)var4.next();
            if (!this.changedComponents.containsKey(component.type())) {
               list.add(component);
            }
         }

         return list.iterator();
      }
   }

   public int size() {
      int i = this.baseComponents.size();
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.changedComponents).iterator();

      while(var2.hasNext()) {
         Reference2ObjectMap.Entry entry = (Reference2ObjectMap.Entry)var2.next();
         boolean bl = ((Optional)entry.getValue()).isPresent();
         boolean bl2 = this.baseComponents.contains((ComponentType)entry.getKey());
         if (bl != bl2) {
            i += bl ? 1 : -1;
         }
      }

      return i;
   }

   public ComponentChanges getChanges() {
      if (this.changedComponents.isEmpty()) {
         return ComponentChanges.EMPTY;
      } else {
         this.copyOnWrite = true;
         return new ComponentChanges(this.changedComponents);
      }
   }

   public MergedComponentMap copy() {
      this.copyOnWrite = true;
      return new MergedComponentMap(this.baseComponents, this.changedComponents, true);
   }

   public ComponentMap immutableCopy() {
      return (ComponentMap)(this.changedComponents.isEmpty() ? this.baseComponents : this.copy());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof MergedComponentMap) {
            MergedComponentMap mergedComponentMap = (MergedComponentMap)o;
            if (this.baseComponents.equals(mergedComponentMap.baseComponents) && this.changedComponents.equals(mergedComponentMap.changedComponents)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.baseComponents.hashCode() + this.changedComponents.hashCode() * 31;
   }

   public String toString() {
      Stream var10000 = this.stream().map(Component::toString);
      return "{" + (String)var10000.collect(Collectors.joining(", ")) + "}";
   }
}
