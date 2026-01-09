package net.minecraft.predicate.component;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public final class ComponentMapPredicate implements Predicate {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final ComponentMapPredicate EMPTY;
   private final List components;

   ComponentMapPredicate(List components) {
      this.components = components;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static ComponentMapPredicate of(ComponentType type, Object value) {
      return new ComponentMapPredicate(List.of(new Component(type, value)));
   }

   public static ComponentMapPredicate of(ComponentMap components) {
      return new ComponentMapPredicate(ImmutableList.copyOf(components));
   }

   public static ComponentMapPredicate ofFiltered(ComponentMap components, ComponentType... types) {
      Builder builder = new Builder();
      ComponentType[] var3 = types;
      int var4 = types.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ComponentType componentType = var3[var5];
         Component component = components.getTyped(componentType);
         if (component != null) {
            builder.add(component);
         }
      }

      return builder.build();
   }

   public boolean isEmpty() {
      return this.components.isEmpty();
   }

   public boolean equals(Object o) {
      boolean var10000;
      if (o instanceof ComponentMapPredicate componentMapPredicate) {
         if (this.components.equals(componentMapPredicate.components)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.components.hashCode();
   }

   public String toString() {
      return this.components.toString();
   }

   public boolean test(ComponentsAccess componentsAccess) {
      Iterator var2 = this.components.iterator();

      Component component;
      Object object;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         component = (Component)var2.next();
         object = componentsAccess.get(component.type());
      } while(Objects.equals(component.value(), object));

      return false;
   }

   public boolean method_57867() {
      return this.components.isEmpty();
   }

   public ComponentChanges toChanges() {
      ComponentChanges.Builder builder = ComponentChanges.builder();
      Iterator var2 = this.components.iterator();

      while(var2.hasNext()) {
         Component component = (Component)var2.next();
         builder.add(component);
      }

      return builder.build();
   }

   // $FF: synthetic method
   public boolean test(final Object components) {
      return this.test((ComponentsAccess)components);
   }

   static {
      CODEC = ComponentType.TYPE_TO_VALUE_MAP_CODEC.xmap((map) -> {
         return new ComponentMapPredicate((List)map.entrySet().stream().map(Component::of).collect(Collectors.toList()));
      }, (predicate) -> {
         return (Map)predicate.components.stream().filter((component) -> {
            return !component.type().shouldSkipSerialization();
         }).collect(Collectors.toMap(Component::type, Component::value));
      });
      PACKET_CODEC = Component.PACKET_CODEC.collect(PacketCodecs.toList()).xmap(ComponentMapPredicate::new, (predicate) -> {
         return predicate.components;
      });
      EMPTY = new ComponentMapPredicate(List.of());
   }

   public static class Builder {
      private final List components = new ArrayList();

      Builder() {
      }

      public Builder add(Component component) {
         return this.add(component.type(), component.value());
      }

      public Builder add(ComponentType type, Object value) {
         Iterator var3 = this.components.iterator();

         Component component;
         do {
            if (!var3.hasNext()) {
               this.components.add(new Component(type, value));
               return this;
            }

            component = (Component)var3.next();
         } while(component.type() != type);

         throw new IllegalArgumentException("Predicate already has component of type: '" + String.valueOf(type) + "'");
      }

      public ComponentMapPredicate build() {
         return new ComponentMapPredicate(List.copyOf(this.components));
      }
   }
}
