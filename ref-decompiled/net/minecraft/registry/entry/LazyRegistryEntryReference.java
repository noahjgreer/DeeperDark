package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public record LazyRegistryEntryReference(Either contents) {
   public LazyRegistryEntryReference(RegistryEntry entry) {
      this(Either.left(entry));
   }

   public LazyRegistryEntryReference(RegistryKey key) {
      this(Either.right(key));
   }

   public LazyRegistryEntryReference(Either either) {
      this.contents = either;
   }

   public static Codec createCodec(RegistryKey registryRef, Codec entryCodec) {
      return Codec.either(entryCodec, RegistryKey.createCodec(registryRef).comapFlatMap((registryKey) -> {
         return DataResult.error(() -> {
            return "Cannot parse as key without registry";
         });
      }, Function.identity())).xmap(LazyRegistryEntryReference::new, LazyRegistryEntryReference::contents);
   }

   public static PacketCodec createPacketCodec(RegistryKey registryRef, PacketCodec entryPacketCodec) {
      return PacketCodec.tuple(PacketCodecs.either(entryPacketCodec, RegistryKey.createPacketCodec(registryRef)), LazyRegistryEntryReference::contents, LazyRegistryEntryReference::new);
   }

   public Optional resolveValue(Registry registry) {
      Either var10000 = this.contents;
      Function var10001 = (entry) -> {
         return Optional.of(entry.value());
      };
      Objects.requireNonNull(registry);
      return (Optional)var10000.map(var10001, registry::getOptionalValue);
   }

   public Optional resolveEntry(RegistryWrapper.WrapperLookup registries) {
      return (Optional)this.contents.map(Optional::of, (key) -> {
         return registries.getOptionalEntry(key).map((entry) -> {
            return entry;
         });
      });
   }

   public Optional getKey() {
      return (Optional)this.contents.map(RegistryEntry::getKey, Optional::of);
   }

   public Either contents() {
      return this.contents;
   }
}
