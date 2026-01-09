package net.minecraft.client.render.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.BlockModelDefinition;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BlockStatesLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceFinder FINDER = ResourceFinder.json("blockstates");

   public static CompletableFuture load(ResourceManager resourceManager, Executor prepareExecutor) {
      Function function = BlockStateManagers.createIdToManagerMapper();
      return CompletableFuture.supplyAsync(() -> {
         return FINDER.findAllResources(resourceManager);
      }, prepareExecutor).thenCompose((resourceMap) -> {
         List list = new ArrayList(resourceMap.size());
         Iterator var4 = resourceMap.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            list.add(CompletableFuture.supplyAsync(() -> {
               Identifier identifier = FINDER.toResourceId((Identifier)entry.getKey());
               StateManager stateManager = (StateManager)function.apply(identifier);
               if (stateManager == null) {
                  LOGGER.debug("Discovered unknown block state definition {}, ignoring", identifier);
                  return null;
               } else {
                  List list = (List)entry.getValue();
                  List list2 = new ArrayList(list.size());
                  Iterator var6 = list.iterator();

                  while(var6.hasNext()) {
                     Resource resource = (Resource)var6.next();

                     try {
                        Reader reader = resource.getReader();

                        try {
                           JsonElement jsonElement = StrictJsonParser.parse((Reader)reader);
                           BlockModelDefinition blockModelDefinition = (BlockModelDefinition)BlockModelDefinition.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
                           list2.add(new LoadedBlockStateDefinition(resource.getPackId(), blockModelDefinition));
                        } catch (Throwable var13) {
                           if (reader != null) {
                              try {
                                 reader.close();
                              } catch (Throwable var12) {
                                 var13.addSuppressed(var12);
                              }
                           }

                           throw var13;
                        }

                        if (reader != null) {
                           reader.close();
                        }
                     } catch (Exception var14) {
                        LOGGER.error("Failed to load blockstate definition {} from pack {}", new Object[]{identifier, resource.getPackId(), var14});
                     }
                  }

                  try {
                     return combine(identifier, stateManager, list2);
                  } catch (Exception var11) {
                     LOGGER.error("Failed to load blockstate definition {}", identifier, var11);
                     return null;
                  }
               }
            }, prepareExecutor));
         }

         return Util.combineSafe(list).thenApply((definitions) -> {
            Map map = new IdentityHashMap();
            Iterator var2 = definitions.iterator();

            while(var2.hasNext()) {
               LoadedModels loadedModels = (LoadedModels)var2.next();
               if (loadedModels != null) {
                  map.putAll(loadedModels.models());
               }
            }

            return new LoadedModels(map);
         });
      });
   }

   private static LoadedModels combine(Identifier id, StateManager stateManager, List definitions) {
      Map map = new IdentityHashMap();
      Iterator var4 = definitions.iterator();

      while(var4.hasNext()) {
         LoadedBlockStateDefinition loadedBlockStateDefinition = (LoadedBlockStateDefinition)var4.next();
         map.putAll(loadedBlockStateDefinition.contents.load(stateManager, () -> {
            String var10000 = String.valueOf(id);
            return var10000 + "/" + loadedBlockStateDefinition.source;
         }));
      }

      return new LoadedModels(map);
   }

   @Environment(EnvType.CLIENT)
   private static record LoadedBlockStateDefinition(String source, BlockModelDefinition contents) {
      final String source;
      final BlockModelDefinition contents;

      LoadedBlockStateDefinition(String string, BlockModelDefinition blockModelDefinition) {
         this.source = string;
         this.contents = blockModelDefinition;
      }

      public String source() {
         return this.source;
      }

      public BlockModelDefinition contents() {
         return this.contents;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record LoadedModels(Map models) {
      public LoadedModels(Map map) {
         this.models = map;
      }

      public Map models() {
         return this.models;
      }
   }
}
