package net.minecraft.registry.tag;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SerializableRegistries;
import net.minecraft.registry.entry.RegistryEntry;

public class TagPacketSerializer {
   public static Map serializeTags(CombinedDynamicRegistries dynamicRegistryManager) {
      return (Map)SerializableRegistries.streamRegistryManagerEntries(dynamicRegistryManager).map((registry) -> {
         return Pair.of(registry.key(), serializeTags(registry.value()));
      }).filter((pair) -> {
         return !((Serialized)pair.getSecond()).isEmpty();
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
   }

   private static Serialized serializeTags(Registry registry) {
      Map map = new HashMap();
      registry.streamTags().forEach((tag) -> {
         IntList intList = new IntArrayList(tag.size());
         Iterator var4 = tag.iterator();

         while(var4.hasNext()) {
            RegistryEntry registryEntry = (RegistryEntry)var4.next();
            if (registryEntry.getType() != RegistryEntry.Type.REFERENCE) {
               throw new IllegalStateException("Can't serialize unregistered value " + String.valueOf(registryEntry));
            }

            intList.add(registry.getRawId(registryEntry.value()));
         }

         map.put(tag.getTag().id(), intList);
      });
      return new Serialized(map);
   }

   static TagGroupLoader.RegistryTags toRegistryTags(Registry registry, Serialized tags) {
      RegistryKey registryKey = registry.getKey();
      Map map = new HashMap();
      tags.contents.forEach((tagId, rawIds) -> {
         TagKey tagKey = TagKey.of(registryKey, tagId);
         IntStream var10000 = rawIds.intStream();
         Objects.requireNonNull(registry);
         List list = (List)var10000.mapToObj(registry::getEntry).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
         map.put(tagKey, list);
      });
      return new TagGroupLoader.RegistryTags(registryKey, map);
   }

   public static final class Serialized {
      public static final Serialized NONE = new Serialized(Map.of());
      final Map contents;

      Serialized(Map contents) {
         this.contents = contents;
      }

      public void writeBuf(PacketByteBuf buf) {
         buf.writeMap(this.contents, PacketByteBuf::writeIdentifier, PacketByteBuf::writeIntList);
      }

      public static Serialized fromBuf(PacketByteBuf buf) {
         return new Serialized(buf.readMap(PacketByteBuf::readIdentifier, PacketByteBuf::readIntList));
      }

      public boolean isEmpty() {
         return this.contents.isEmpty();
      }

      public int size() {
         return this.contents.size();
      }

      public TagGroupLoader.RegistryTags toRegistryTags(Registry registry) {
         return TagPacketSerializer.toRegistryTags(registry, this);
      }
   }
}
