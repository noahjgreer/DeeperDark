package net.minecraft.client.item;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.registry.ContextSwappableRegistryLookup;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ItemAssetsLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceFinder FINDER = ResourceFinder.json("items");

   public static CompletableFuture load(ResourceManager resourceManager, Executor executor) {
      DynamicRegistryManager.Immutable immutable = ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager();
      return CompletableFuture.supplyAsync(() -> {
         return FINDER.findResources(resourceManager);
      }, executor).thenCompose((itemAssets) -> {
         List list = new ArrayList(itemAssets.size());
         itemAssets.forEach((itemId, itemResource) -> {
            list.add(CompletableFuture.supplyAsync(() -> {
               Identifier identifier2 = FINDER.toResourceId(itemId);

               try {
                  Reader reader = itemResource.getReader();

                  Definition var8;
                  try {
                     ContextSwappableRegistryLookup contextSwappableRegistryLookup = new ContextSwappableRegistryLookup(immutable);
                     DynamicOps dynamicOps = contextSwappableRegistryLookup.createRegistryOps(JsonOps.INSTANCE);
                     ItemAsset itemAsset = (ItemAsset)ItemAsset.CODEC.parse(dynamicOps, StrictJsonParser.parse((Reader)reader)).ifError((error) -> {
                        LOGGER.error("Couldn't parse item model '{}' from pack '{}': {}", new Object[]{identifier2, itemResource.getPackId(), error.message()});
                     }).result().map((itemAssetx) -> {
                        return contextSwappableRegistryLookup.hasEntries() ? itemAssetx.withContextSwapper(contextSwappableRegistryLookup.createContextSwapper()) : itemAssetx;
                     }).orElse((Object)null);
                     var8 = new Definition(identifier2, itemAsset);
                  } catch (Throwable var10) {
                     if (reader != null) {
                        try {
                           reader.close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }
                     }

                     throw var10;
                  }

                  if (reader != null) {
                     reader.close();
                  }

                  return var8;
               } catch (Exception var11) {
                  LOGGER.error("Failed to open item model {} from pack '{}'", new Object[]{itemId, itemResource.getPackId(), var11});
                  return new Definition(identifier2, (ItemAsset)null);
               }
            }, executor));
         });
         return Util.combineSafe(list).thenApply((definitions) -> {
            Map map = new HashMap();
            Iterator var2 = definitions.iterator();

            while(var2.hasNext()) {
               Definition definition = (Definition)var2.next();
               if (definition.clientItemInfo != null) {
                  map.put(definition.id, definition.clientItemInfo);
               }
            }

            return new Result(map);
         });
      });
   }

   @Environment(EnvType.CLIENT)
   private static record Definition(Identifier id, @Nullable ItemAsset clientItemInfo) {
      final Identifier id;
      @Nullable
      final ItemAsset clientItemInfo;

      Definition(Identifier identifier, @Nullable ItemAsset itemAsset) {
         this.id = identifier;
         this.clientItemInfo = itemAsset;
      }

      public Identifier id() {
         return this.id;
      }

      @Nullable
      public ItemAsset clientItemInfo() {
         return this.clientItemInfo;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Result(Map contents) {
      public Result(Map map) {
         this.contents = map;
      }

      public Map contents() {
         return this.contents;
      }
   }
}
