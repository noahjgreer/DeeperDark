package net.minecraft.predicate.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;

public interface ComponentPredicate {
   Codec PREDICATES_MAP_CODEC = Codec.dispatchedMap(Registries.DATA_COMPONENT_PREDICATE_TYPE.getCodec(), Type::getPredicateCodec);
   PacketCodec SINGLE_PREDICATE_PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE).dispatch(Typed::type, Type::getTypedPacketCodec);
   PacketCodec PREDICATES_MAP_PACKET_CODEC = SINGLE_PREDICATE_PACKET_CODEC.collect(PacketCodecs.toList(64)).xmap((list) -> {
      return (Map)list.stream().collect(Collectors.toMap(Typed::type, Typed::predicate));
   }, (map) -> {
      return map.entrySet().stream().map(Typed::fromEntry).toList();
   });

   static MapCodec createCodec(String predicateFieldName) {
      return Registries.DATA_COMPONENT_PREDICATE_TYPE.getCodec().dispatchMap(predicateFieldName, Typed::type, Type::getTypedCodec);
   }

   boolean test(ComponentsAccess components);

   public static record Typed(Type type, ComponentPredicate predicate) {
      public Typed(Type type, ComponentPredicate componentPredicate) {
         this.type = type;
         this.predicate = componentPredicate;
      }

      private static Typed fromEntry(Map.Entry entry) {
         return new Typed((Type)entry.getKey(), (ComponentPredicate)entry.getValue());
      }

      public Type type() {
         return this.type;
      }

      public ComponentPredicate predicate() {
         return this.predicate;
      }
   }

   public static final class Type {
      private final Codec predicateCodec;
      private final MapCodec typedCodec;
      private final PacketCodec typedPacketCodec;

      public Type(Codec predicateCodec) {
         this.predicateCodec = predicateCodec;
         this.typedCodec = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(predicateCodec.fieldOf("value").forGetter(Typed::predicate)).apply(instance, (predicate) -> {
               return new Typed(this, predicate);
            });
         });
         this.typedPacketCodec = PacketCodecs.registryCodec(predicateCodec).xmap((predicate) -> {
            return new Typed(this, predicate);
         }, Typed::predicate);
      }

      public Codec getPredicateCodec() {
         return this.predicateCodec;
      }

      public MapCodec getTypedCodec() {
         return this.typedCodec;
      }

      public PacketCodec getTypedPacketCodec() {
         return this.typedPacketCodec;
      }
   }
}
