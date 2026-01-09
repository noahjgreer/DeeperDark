package net.minecraft.registry;

import com.mojang.serialization.DynamicOps;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.util.Identifier;

public class SerializableRegistries {
   private static final Set SYNCED_REGISTRIES;

   public static void forEachSyncedRegistry(DynamicOps nbtOps, DynamicRegistryManager registryManager, Set knownPacks, BiConsumer callback) {
      RegistryLoader.SYNCED_REGISTRIES.forEach((registry) -> {
         serialize(nbtOps, registry, registryManager, knownPacks, callback);
      });
   }

   private static void serialize(DynamicOps nbtOps, RegistryLoader.Entry entry, DynamicRegistryManager registryManager, Set knownPacks, BiConsumer callback) {
      registryManager.getOptional(entry.key()).ifPresent((registry) -> {
         List list = new ArrayList(registry.size());
         registry.streamEntries().forEach((registryEntry) -> {
            Optional var10000 = registry.getEntryInfo(registryEntry.registryKey()).flatMap(RegistryEntryInfo::knownPackInfo);
            Objects.requireNonNull(knownPacks);
            boolean bl = var10000.filter(knownPacks::contains).isPresent();
            Optional optional;
            if (bl) {
               optional = Optional.empty();
            } else {
               NbtElement nbtElement = (NbtElement)entry.elementCodec().encodeStart(nbtOps, registryEntry.value()).getOrThrow((error) -> {
                  String var10002 = String.valueOf(registryEntry.registryKey());
                  return new IllegalArgumentException("Failed to serialize " + var10002 + ": " + error);
               });
               optional = Optional.of(nbtElement);
            }

            list.add(new SerializedRegistryEntry(registryEntry.registryKey().getValue(), optional));
         });
         callback.accept(registry.getKey(), list);
      });
   }

   private static Stream stream(DynamicRegistryManager dynamicRegistryManager) {
      return dynamicRegistryManager.streamAllRegistries().filter((registry) -> {
         return isSynced(registry.key());
      });
   }

   public static Stream streamDynamicEntries(CombinedDynamicRegistries combinedRegistries) {
      return stream(combinedRegistries.getSucceedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN));
   }

   public static Stream streamRegistryManagerEntries(CombinedDynamicRegistries combinedRegistries) {
      Stream stream = combinedRegistries.get(ServerDynamicRegistryType.STATIC).streamAllRegistries();
      Stream stream2 = streamDynamicEntries(combinedRegistries);
      return Stream.concat(stream2, stream);
   }

   public static boolean isSynced(RegistryKey key) {
      return SYNCED_REGISTRIES.contains(key);
   }

   static {
      SYNCED_REGISTRIES = (Set)RegistryLoader.SYNCED_REGISTRIES.stream().map(RegistryLoader.Entry::key).collect(Collectors.toUnmodifiableSet());
   }

   public static record SerializedRegistryEntry(Identifier id, Optional data) {
      public static final PacketCodec PACKET_CODEC;

      public SerializedRegistryEntry(Identifier identifier, Optional optional) {
         this.id = identifier;
         this.data = optional;
      }

      public Identifier id() {
         return this.id;
      }

      public Optional data() {
         return this.data;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, SerializedRegistryEntry::id, PacketCodecs.NBT_ELEMENT.collect(PacketCodecs::optional), SerializedRegistryEntry::data, SerializedRegistryEntry::new);
      }
   }
}
