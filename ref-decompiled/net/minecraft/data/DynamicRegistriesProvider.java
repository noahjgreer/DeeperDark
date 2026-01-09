package net.minecraft.data;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryWrapper;

public class DynamicRegistriesProvider implements DataProvider {
   private final DataOutput output;
   private final CompletableFuture registriesFuture;

   public DynamicRegistriesProvider(DataOutput output, CompletableFuture registriesFuture) {
      this.registriesFuture = registriesFuture;
      this.output = output;
   }

   public CompletableFuture run(DataWriter writer) {
      return this.registriesFuture.thenCompose((registries) -> {
         DynamicOps dynamicOps = registries.getOps(JsonOps.INSTANCE);
         return CompletableFuture.allOf((CompletableFuture[])RegistryLoader.DYNAMIC_REGISTRIES.stream().flatMap((entry) -> {
            return this.writeRegistryEntries(writer, registries, dynamicOps, entry).stream();
         }).toArray((i) -> {
            return new CompletableFuture[i];
         }));
      });
   }

   private Optional writeRegistryEntries(DataWriter writer, RegistryWrapper.WrapperLookup registries, DynamicOps ops, RegistryLoader.Entry registry) {
      RegistryKey registryKey = registry.key();
      return registries.getOptional(registryKey).map((wrapper) -> {
         DataOutput.PathResolver pathResolver = this.output.getResolver(registryKey);
         return CompletableFuture.allOf((CompletableFuture[])wrapper.streamEntries().map((entry) -> {
            return writeToPath(pathResolver.resolveJson(entry.registryKey().getValue()), writer, ops, registry.elementCodec(), entry.value());
         }).toArray((i) -> {
            return new CompletableFuture[i];
         }));
      });
   }

   private static CompletableFuture writeToPath(Path path, DataWriter cache, DynamicOps json, Encoder encoder, Object value) {
      return (CompletableFuture)encoder.encodeStart(json, value).mapOrElse((jsonElement) -> {
         return DataProvider.writeToPath(cache, jsonElement, path);
      }, (error) -> {
         String var10002 = String.valueOf(path);
         return CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + var10002 + "': " + error.message()));
      });
   }

   public String getName() {
      return "Registries";
   }
}
