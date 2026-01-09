package net.minecraft.component;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.fabricmc.fabric.api.item.v1.FabricComponentMapBuilder;
import org.jetbrains.annotations.Nullable;

public interface ComponentMap extends Iterable, ComponentsAccess {
   ComponentMap EMPTY = new ComponentMap() {
      @Nullable
      public Object get(ComponentType type) {
         return null;
      }

      public Set getTypes() {
         return Set.of();
      }

      public Iterator iterator() {
         return Collections.emptyIterator();
      }
   };
   Codec CODEC = createCodecFromValueMap(ComponentType.TYPE_TO_VALUE_MAP_CODEC);

   static Codec createCodec(Codec componentTypeCodec) {
      return createCodecFromValueMap(Codec.dispatchedMap(componentTypeCodec, ComponentType::getCodecOrThrow));
   }

   static Codec createCodecFromValueMap(Codec typeToValueMapCodec) {
      return typeToValueMapCodec.flatComapMap(Builder::build, (componentMap) -> {
         int i = componentMap.size();
         if (i == 0) {
            return DataResult.success(Reference2ObjectMaps.emptyMap());
         } else {
            Reference2ObjectMap reference2ObjectMap = new Reference2ObjectArrayMap(i);
            Iterator var3 = componentMap.iterator();

            while(var3.hasNext()) {
               Component component = (Component)var3.next();
               if (!component.type().shouldSkipSerialization()) {
                  reference2ObjectMap.put(component.type(), component.value());
               }
            }

            return DataResult.success(reference2ObjectMap);
         }
      });
   }

   static ComponentMap of(final ComponentMap base, final ComponentMap overrides) {
      return new ComponentMap() {
         @Nullable
         public Object get(ComponentType type) {
            Object object = overrides.get(type);
            return object != null ? object : base.get(type);
         }

         public Set getTypes() {
            return Sets.union(base.getTypes(), overrides.getTypes());
         }
      };
   }

   static Builder builder() {
      return new Builder();
   }

   Set getTypes();

   default boolean contains(ComponentType type) {
      return this.get(type) != null;
   }

   default Iterator iterator() {
      return Iterators.transform(this.getTypes().iterator(), (type) -> {
         return (Component)Objects.requireNonNull(this.getTyped(type));
      });
   }

   default Stream stream() {
      return StreamSupport.stream(Spliterators.spliterator(this.iterator(), (long)this.size(), 1345), false);
   }

   default int size() {
      return this.getTypes().size();
   }

   default boolean isEmpty() {
      return this.size() == 0;
   }

   default ComponentMap filtered(final Predicate predicate) {
      return new ComponentMap() {
         @Nullable
         public Object get(ComponentType type) {
            return predicate.test(type) ? ComponentMap.this.get(type) : null;
         }

         public Set getTypes() {
            Set var10000 = ComponentMap.this.getTypes();
            Predicate var10001 = predicate;
            Objects.requireNonNull(var10001);
            return Sets.filter(var10000, var10001::test);
         }
      };
   }

   public static class Builder implements FabricComponentMapBuilder {
      private final Reference2ObjectMap components = new Reference2ObjectArrayMap();

      Builder() {
      }

      public Builder add(ComponentType type, @Nullable Object value) {
         this.put(type, value);
         return this;
      }

      void put(ComponentType type, @Nullable Object value) {
         if (value != null) {
            this.components.put(type, value);
         } else {
            this.components.remove(type);
         }

      }

      public Builder addAll(ComponentMap componentSet) {
         Iterator var2 = componentSet.iterator();

         while(var2.hasNext()) {
            Component component = (Component)var2.next();
            this.components.put(component.type(), component.value());
         }

         return this;
      }

      public ComponentMap build() {
         return build(this.components);
      }

      private static ComponentMap build(Map components) {
         if (components.isEmpty()) {
            return ComponentMap.EMPTY;
         } else {
            return components.size() < 8 ? new SimpleComponentMap(new Reference2ObjectArrayMap(components)) : new SimpleComponentMap(new Reference2ObjectOpenHashMap(components));
         }
      }

      private static record SimpleComponentMap(Reference2ObjectMap map) implements ComponentMap {
         SimpleComponentMap(Reference2ObjectMap reference2ObjectMap) {
            this.map = reference2ObjectMap;
         }

         @Nullable
         public Object get(ComponentType type) {
            return this.map.get(type);
         }

         public boolean contains(ComponentType type) {
            return this.map.containsKey(type);
         }

         public Set getTypes() {
            return this.map.keySet();
         }

         public Iterator iterator() {
            return Iterators.transform(Reference2ObjectMaps.fastIterator(this.map), Component::of);
         }

         public int size() {
            return this.map.size();
         }

         public String toString() {
            return this.map.toString();
         }

         public Reference2ObjectMap map() {
            return this.map;
         }
      }
   }
}
