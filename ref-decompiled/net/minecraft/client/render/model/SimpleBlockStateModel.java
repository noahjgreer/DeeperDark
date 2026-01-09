package net.minecraft.client.render.model;

import com.mojang.serialization.Codec;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class SimpleBlockStateModel implements BlockStateModel {
   private final BlockModelPart part;

   public SimpleBlockStateModel(BlockModelPart part) {
      this.part = part;
   }

   public void addParts(Random random, List parts) {
      parts.add(this.part);
   }

   public Sprite particleSprite() {
      return this.part.particleSprite();
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(ModelVariant variant) implements BlockStateModel.Unbaked {
      public static final Codec CODEC;

      public Unbaked(ModelVariant modelVariant) {
         this.variant = modelVariant;
      }

      public BlockStateModel bake(Baker baker) {
         return new SimpleBlockStateModel(this.variant.bake(baker));
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         this.variant.resolve(resolver);
      }

      public ModelVariant variant() {
         return this.variant;
      }

      static {
         CODEC = ModelVariant.CODEC.xmap(Unbaked::new, Unbaked::variant);
      }
   }
}
