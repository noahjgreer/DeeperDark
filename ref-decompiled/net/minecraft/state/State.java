package net.minecraft.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

public abstract class State {
   public static final String NAME = "Name";
   public static final String PROPERTIES = "Properties";
   private static final Function PROPERTY_MAP_PRINTER = new Function() {
      public String apply(@Nullable Map.Entry entry) {
         if (entry == null) {
            return "<NULL>";
         } else {
            Property property = (Property)entry.getKey();
            String var10000 = property.getName();
            return var10000 + "=" + this.nameValue(property, (Comparable)entry.getValue());
         }
      }

      private String nameValue(Property property, Comparable value) {
         return property.name(value);
      }

      // $FF: synthetic method
      public Object apply(@Nullable final Object entry) {
         return this.apply((Map.Entry)entry);
      }
   };
   protected final Object owner;
   private final Reference2ObjectArrayMap propertyMap;
   private Map withMap;
   protected final MapCodec codec;

   protected State(Object owner, Reference2ObjectArrayMap propertyMap, MapCodec codec) {
      this.owner = owner;
      this.propertyMap = propertyMap;
      this.codec = codec;
   }

   public Object cycle(Property property) {
      return this.with(property, (Comparable)getNext(property.getValues(), this.get(property)));
   }

   protected static Object getNext(List values, Object value) {
      int i = values.indexOf(value) + 1;
      return i == values.size() ? values.getFirst() : values.get(i);
   }

   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(this.owner);
      if (!this.getEntries().isEmpty()) {
         stringBuilder.append('[');
         stringBuilder.append((String)this.getEntries().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(",")));
         stringBuilder.append(']');
      }

      return stringBuilder.toString();
   }

   public final boolean equals(Object object) {
      return super.equals(object);
   }

   public int hashCode() {
      return super.hashCode();
   }

   public Collection getProperties() {
      return Collections.unmodifiableCollection(this.propertyMap.keySet());
   }

   public boolean contains(Property property) {
      return this.propertyMap.containsKey(property);
   }

   public Comparable get(Property property) {
      Comparable comparable = (Comparable)this.propertyMap.get(property);
      if (comparable == null) {
         String var10002 = String.valueOf(property);
         throw new IllegalArgumentException("Cannot get property " + var10002 + " as it does not exist in " + String.valueOf(this.owner));
      } else {
         return (Comparable)property.getType().cast(comparable);
      }
   }

   public Optional getOrEmpty(Property property) {
      return Optional.ofNullable(this.getNullable(property));
   }

   public Comparable get(Property property, Comparable fallback) {
      return (Comparable)Objects.requireNonNullElse(this.getNullable(property), fallback);
   }

   @Nullable
   private Comparable getNullable(Property property) {
      Comparable comparable = (Comparable)this.propertyMap.get(property);
      return comparable == null ? null : (Comparable)property.getType().cast(comparable);
   }

   public Object with(Property property, Comparable value) {
      Comparable comparable = (Comparable)this.propertyMap.get(property);
      if (comparable == null) {
         String var10002 = String.valueOf(property);
         throw new IllegalArgumentException("Cannot set property " + var10002 + " as it does not exist in " + String.valueOf(this.owner));
      } else {
         return this.with(property, value, comparable);
      }
   }

   public Object withIfExists(Property property, Comparable value) {
      Comparable comparable = (Comparable)this.propertyMap.get(property);
      return comparable == null ? this : this.with(property, value, comparable);
   }

   private Object with(Property property, Comparable newValue, Comparable oldValue) {
      if (oldValue.equals(newValue)) {
         return this;
      } else {
         int i = property.ordinal(newValue);
         if (i < 0) {
            String var10002 = String.valueOf(property);
            throw new IllegalArgumentException("Cannot set property " + var10002 + " to " + String.valueOf(newValue) + " on " + String.valueOf(this.owner) + ", it is not an allowed value");
         } else {
            return ((Object[])this.withMap.get(property))[i];
         }
      }
   }

   public void createWithMap(Map states) {
      if (this.withMap != null) {
         throw new IllegalStateException();
      } else {
         Map map = new Reference2ObjectArrayMap(this.propertyMap.size());
         ObjectIterator var3 = this.propertyMap.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry entry = (Map.Entry)var3.next();
            Property property = (Property)entry.getKey();
            map.put(property, property.getValues().stream().map((value) -> {
               return states.get(this.toMapWith(property, value));
            }).toArray());
         }

         this.withMap = map;
      }
   }

   private Map toMapWith(Property property, Comparable value) {
      Map map = new Reference2ObjectArrayMap(this.propertyMap);
      map.put(property, value);
      return map;
   }

   public Map getEntries() {
      return this.propertyMap;
   }

   protected static Codec createCodec(Codec codec, Function ownerToStateFunction) {
      return codec.dispatch("Name", (state) -> {
         return state.owner;
      }, (owner) -> {
         State state = (State)ownerToStateFunction.apply(owner);
         return state.getEntries().isEmpty() ? MapCodec.unit(state) : state.codec.codec().lenientOptionalFieldOf("Properties").xmap((statex) -> {
            return (State)statex.orElse(state);
         }, Optional::of);
      });
   }
}
