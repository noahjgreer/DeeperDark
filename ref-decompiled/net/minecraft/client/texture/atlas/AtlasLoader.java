package net.minecraft.client.texture.atlas;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.StrictJsonParser;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class AtlasLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceFinder FINDER = new ResourceFinder("atlases", ".json");
   private final List sources;

   private AtlasLoader(List sources) {
      this.sources = sources;
   }

   public List loadSources(ResourceManager resourceManager) {
      final Map map = new HashMap();
      AtlasSource.SpriteRegions spriteRegions = new AtlasSource.SpriteRegions(this) {
         public void add(Identifier arg, AtlasSource.SpriteRegion region) {
            AtlasSource.SpriteRegion spriteRegion = (AtlasSource.SpriteRegion)map.put(arg, region);
            if (spriteRegion != null) {
               spriteRegion.close();
            }

         }

         public void removeIf(Predicate predicate) {
            Iterator iterator = map.entrySet().iterator();

            while(iterator.hasNext()) {
               Map.Entry entry = (Map.Entry)iterator.next();
               if (predicate.test((Identifier)entry.getKey())) {
                  ((AtlasSource.SpriteRegion)entry.getValue()).close();
                  iterator.remove();
               }
            }

         }
      };
      this.sources.forEach((source) -> {
         source.load(resourceManager, spriteRegions);
      });
      ImmutableList.Builder builder = ImmutableList.builder();
      builder.add((opener) -> {
         return MissingSprite.createSpriteContents();
      });
      builder.addAll(map.values());
      return builder.build();
   }

   public static AtlasLoader of(ResourceManager resourceManager, Identifier id) {
      Identifier identifier = FINDER.toResourcePath(id);
      List list = new ArrayList();
      Iterator var4 = resourceManager.getAllResources(identifier).iterator();

      while(var4.hasNext()) {
         Resource resource = (Resource)var4.next();

         try {
            BufferedReader bufferedReader = resource.getReader();

            try {
               Dynamic dynamic = new Dynamic(JsonOps.INSTANCE, StrictJsonParser.parse((Reader)bufferedReader));
               list.addAll((Collection)AtlasSourceManager.LIST_CODEC.parse(dynamic).getOrThrow());
            } catch (Throwable var10) {
               if (bufferedReader != null) {
                  try {
                     bufferedReader.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (bufferedReader != null) {
               bufferedReader.close();
            }
         } catch (Exception var11) {
            LOGGER.error("Failed to parse atlas definition {} in pack {}", new Object[]{identifier, resource.getPackId(), var11});
         }
      }

      return new AtlasLoader(list);
   }
}
