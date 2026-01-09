package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import net.minecraft.component.ComponentMap;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;

public class ItemListProvider implements DataProvider {
   private final DataOutput output;
   private final CompletableFuture registriesFuture;

   public ItemListProvider(DataOutput output, CompletableFuture registriesFuture) {
      this.output = output;
      this.registriesFuture = registriesFuture;
   }

   public CompletableFuture run(DataWriter writer) {
      Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("items.json");
      return this.registriesFuture.thenCompose((registries) -> {
         JsonObject jsonObject = new JsonObject();
         RegistryOps registryOps = registries.getOps(JsonOps.INSTANCE);
         registries.getOrThrow(RegistryKeys.ITEM).streamEntries().forEach((entry) -> {
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.add("components", (JsonElement)ComponentMap.CODEC.encodeStart(registryOps, ((Item)entry.value()).getComponents()).getOrThrow((components) -> {
               return new IllegalStateException("Failed to encode components: " + components);
            }));
            jsonObject.add(entry.getIdAsString(), jsonObject2);
         });
         return DataProvider.writeToPath(writer, jsonObject, path);
      });
   }

   public String getName() {
      return "Item List";
   }
}
