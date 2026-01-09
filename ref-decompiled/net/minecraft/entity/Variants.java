package net.minecraft.entity;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.world.ServerWorldAccess;

public class Variants {
   public static final String VARIANT_NBT_KEY = "variant";

   public static RegistryEntry getOrDefaultOrThrow(DynamicRegistryManager registries, RegistryKey variantKey) {
      Registry registry = registries.getOrThrow(variantKey.getRegistryRef());
      Optional var10000 = registry.getOptional(variantKey);
      Objects.requireNonNull(registry);
      return (RegistryEntry)var10000.or(registry::getDefaultEntry).orElseThrow();
   }

   public static RegistryEntry getDefaultOrThrow(DynamicRegistryManager registries, RegistryKey registryRef) {
      return (RegistryEntry)registries.getOrThrow(registryRef).getDefaultEntry().orElseThrow();
   }

   public static void writeVariantToNbt(WriteView view, RegistryEntry variantEntry) {
      variantEntry.getKey().ifPresent((key) -> {
         view.put("variant", Identifier.CODEC, key.getValue());
      });
   }

   public static Optional readVariantFromNbt(ReadView view, RegistryKey registryRef) {
      Optional var10000 = view.read("variant", Identifier.CODEC).map((id) -> {
         return RegistryKey.of(registryRef, id);
      });
      RegistryWrapper.WrapperLookup var10001 = view.getRegistries();
      Objects.requireNonNull(var10001);
      return var10000.flatMap(var10001::getOptionalEntry);
   }

   public static Optional select(SpawnContext context, RegistryKey registryRef) {
      ServerWorldAccess serverWorldAccess = context.world();
      Stream stream = serverWorldAccess.getRegistryManager().getOrThrow(registryRef).streamEntries();
      return VariantSelectorProvider.select(stream, RegistryEntry::value, serverWorldAccess.getRandom(), context);
   }
}
