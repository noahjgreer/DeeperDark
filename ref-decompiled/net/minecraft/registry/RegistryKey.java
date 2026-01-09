package net.minecraft.registry;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public class RegistryKey {
   private static final ConcurrentMap INSTANCES = (new MapMaker()).weakValues().makeMap();
   private final Identifier registry;
   private final Identifier value;

   public static Codec createCodec(RegistryKey registry) {
      return Identifier.CODEC.xmap((id) -> {
         return of(registry, id);
      }, RegistryKey::getValue);
   }

   public static PacketCodec createPacketCodec(RegistryKey registry) {
      return Identifier.PACKET_CODEC.xmap((id) -> {
         return of(registry, id);
      }, RegistryKey::getValue);
   }

   public static RegistryKey of(RegistryKey registry, Identifier value) {
      return of(registry.value, value);
   }

   public static RegistryKey ofRegistry(Identifier registry) {
      return of(RegistryKeys.ROOT, registry);
   }

   private static RegistryKey of(Identifier registry, Identifier value) {
      return (RegistryKey)INSTANCES.computeIfAbsent(new RegistryIdPair(registry, value), (pair) -> {
         return new RegistryKey(pair.registry, pair.id);
      });
   }

   private RegistryKey(Identifier registry, Identifier value) {
      this.registry = registry;
      this.value = value;
   }

   public String toString() {
      String var10000 = String.valueOf(this.registry);
      return "ResourceKey[" + var10000 + " / " + String.valueOf(this.value) + "]";
   }

   public boolean isOf(RegistryKey registry) {
      return this.registry.equals(registry.getValue());
   }

   public Optional tryCast(RegistryKey registryRef) {
      return this.isOf(registryRef) ? Optional.of(this) : Optional.empty();
   }

   public Identifier getValue() {
      return this.value;
   }

   public Identifier getRegistry() {
      return this.registry;
   }

   public RegistryKey getRegistryRef() {
      return ofRegistry(this.registry);
   }

   private static record RegistryIdPair(Identifier registry, Identifier id) {
      final Identifier registry;
      final Identifier id;

      RegistryIdPair(Identifier identifier, Identifier identifier2) {
         this.registry = identifier;
         this.id = identifier2;
      }

      public Identifier registry() {
         return this.registry;
      }

      public Identifier id() {
         return this.id;
      }
   }
}
