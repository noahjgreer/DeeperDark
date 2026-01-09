package net.minecraft.client.texture.atlas;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface AtlasSource {
   ResourceFinder RESOURCE_FINDER = new ResourceFinder("textures", ".png");

   void load(ResourceManager resourceManager, SpriteRegions regions);

   MapCodec getCodec();

   @Environment(EnvType.CLIENT)
   public interface SpriteRegion extends Function {
      default void close() {
      }
   }

   @Environment(EnvType.CLIENT)
   public interface SpriteRegions {
      default void add(Identifier id, Resource resource) {
         this.add(id, (opener) -> {
            return opener.loadSprite(id, resource);
         });
      }

      void add(Identifier arg, SpriteRegion region);

      void removeIf(Predicate predicate);
   }
}
