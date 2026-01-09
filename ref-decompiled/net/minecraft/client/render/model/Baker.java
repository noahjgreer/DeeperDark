package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface Baker {
   BakedSimpleModel getModel(Identifier id);

   ErrorCollectingSpriteGetter getSpriteGetter();

   Object compute(ResolvableCacheKey key);

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface ResolvableCacheKey {
      Object compute(Baker baker);
   }
}
