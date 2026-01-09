package net.minecraft.data.report;

import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockTypes;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

public class BlockListProvider implements DataProvider {
   private final DataOutput output;
   private final CompletableFuture registriesFuture;

   public BlockListProvider(DataOutput output, CompletableFuture registriesFuture) {
      this.output = output;
      this.registriesFuture = registriesFuture;
   }

   public CompletableFuture run(DataWriter writer) {
      Path path = this.output.resolvePath(DataOutput.OutputType.REPORTS).resolve("blocks.json");
      return this.registriesFuture.thenCompose((registries) -> {
         JsonObject jsonObject = new JsonObject();
         RegistryOps registryOps = registries.getOps(JsonOps.INSTANCE);
         registries.getOrThrow(RegistryKeys.BLOCK).streamEntries().forEach((entry) -> {
            JsonObject jsonObject2 = new JsonObject();
            StateManager stateManager = ((Block)entry.value()).getStateManager();
            if (!stateManager.getProperties().isEmpty()) {
               JsonObject jsonObject3 = new JsonObject();
               Iterator var6 = stateManager.getProperties().iterator();

               while(var6.hasNext()) {
                  Property property = (Property)var6.next();
                  JsonArray jsonArray = new JsonArray();
                  Iterator var9 = property.getValues().iterator();

                  while(var9.hasNext()) {
                     Comparable comparable = (Comparable)var9.next();
                     jsonArray.add(Util.getValueAsString(property, comparable));
                  }

                  jsonObject3.add(property.getName(), jsonArray);
               }

               jsonObject2.add("properties", jsonObject3);
            }

            JsonArray jsonArray2 = new JsonArray();

            JsonObject jsonObject4;
            for(UnmodifiableIterator var13 = stateManager.getStates().iterator(); var13.hasNext(); jsonArray2.add(jsonObject4)) {
               BlockState blockState = (BlockState)var13.next();
               jsonObject4 = new JsonObject();
               JsonObject jsonObject5 = new JsonObject();
               Iterator var19 = stateManager.getProperties().iterator();

               while(var19.hasNext()) {
                  Property property2 = (Property)var19.next();
                  jsonObject5.addProperty(property2.getName(), Util.getValueAsString(property2, blockState.get(property2)));
               }

               if (jsonObject5.size() > 0) {
                  jsonObject4.add("properties", jsonObject5);
               }

               jsonObject4.addProperty("id", Block.getRawIdFromState(blockState));
               if (blockState == ((Block)entry.value()).getDefaultState()) {
                  jsonObject4.addProperty("default", true);
               }
            }

            jsonObject2.add("states", jsonArray2);
            String string = entry.getIdAsString();
            JsonElement jsonElement = (JsonElement)BlockTypes.CODEC.codec().encodeStart(registryOps, (Block)entry.value()).getOrThrow((string2) -> {
               return new AssertionError("Failed to serialize block " + string + " (is type registered in BlockTypes?): " + string2);
            });
            jsonObject2.add("definition", jsonElement);
            jsonObject.add(string, jsonObject2);
         });
         return DataProvider.writeToPath(writer, jsonObject, path);
      });
   }

   public String getName() {
      return "Block List";
   }
}
