package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class WeightedBlockStateModel implements BlockStateModel {
   private final Pool models;
   private final Sprite particleSprite;

   public WeightedBlockStateModel(Pool models) {
      this.models = models;
      BlockStateModel blockStateModel = (BlockStateModel)((Weighted)models.getEntries().getFirst()).value();
      this.particleSprite = blockStateModel.particleSprite();
   }

   public Sprite particleSprite() {
      return this.particleSprite;
   }

   public void addParts(Random random, List parts) {
      ((BlockStateModel)this.models.get(random)).addParts(random, parts);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(Pool entries) implements BlockStateModel.Unbaked {
      public Unbaked(Pool pool) {
         this.entries = pool;
      }

      public BlockStateModel bake(Baker baker) {
         return new WeightedBlockStateModel(this.entries.transform((model) -> {
            return model.bake(baker);
         }));
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         this.entries.getEntries().forEach((entry) -> {
            ((BlockStateModel.Unbaked)entry.value()).resolve(resolver);
         });
      }

      public Pool entries() {
         return this.entries;
      }
   }
}
