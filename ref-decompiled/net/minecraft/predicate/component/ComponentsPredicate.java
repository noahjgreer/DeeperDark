package net.minecraft.predicate.component;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.network.codec.PacketCodec;

public record ComponentsPredicate(ComponentMapPredicate exact, Map partial) implements Predicate {
   public static final ComponentsPredicate EMPTY;
   public static final MapCodec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public ComponentsPredicate(ComponentMapPredicate componentMapPredicate, Map map) {
      this.exact = componentMapPredicate;
      this.partial = map;
   }

   public boolean test(ComponentsAccess componentsAccess) {
      if (!this.exact.test(componentsAccess)) {
         return false;
      } else {
         Iterator var2 = this.partial.values().iterator();

         ComponentPredicate componentPredicate;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            componentPredicate = (ComponentPredicate)var2.next();
         } while(componentPredicate.test(componentsAccess));

         return false;
      }
   }

   public boolean isEmpty() {
      return this.exact.isEmpty() && this.partial.isEmpty();
   }

   public ComponentMapPredicate exact() {
      return this.exact;
   }

   public Map partial() {
      return this.partial;
   }

   // $FF: synthetic method
   public boolean test(final Object components) {
      return this.test((ComponentsAccess)components);
   }

   static {
      EMPTY = new ComponentsPredicate(ComponentMapPredicate.EMPTY, Map.of());
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(ComponentMapPredicate.CODEC.optionalFieldOf("components", ComponentMapPredicate.EMPTY).forGetter(ComponentsPredicate::exact), ComponentPredicate.PREDICATES_MAP_CODEC.optionalFieldOf("predicates", Map.of()).forGetter(ComponentsPredicate::partial)).apply(instance, ComponentsPredicate::new);
      });
      PACKET_CODEC = PacketCodec.tuple(ComponentMapPredicate.PACKET_CODEC, ComponentsPredicate::exact, ComponentPredicate.PREDICATES_MAP_PACKET_CODEC, ComponentsPredicate::partial, ComponentsPredicate::new);
   }

   public static class Builder {
      private ComponentMapPredicate exact;
      private final ImmutableMap.Builder partial;

      private Builder() {
         this.exact = ComponentMapPredicate.EMPTY;
         this.partial = ImmutableMap.builder();
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder partial(ComponentPredicate.Type type, ComponentPredicate predicate) {
         this.partial.put(type, predicate);
         return this;
      }

      public Builder exact(ComponentMapPredicate exact) {
         this.exact = exact;
         return this;
      }

      public ComponentsPredicate build() {
         return new ComponentsPredicate(this.exact, this.partial.buildOrThrow());
      }
   }
}
