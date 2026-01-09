package net.minecraft.client.render.model;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record GeometryBakedModel(BakedGeometry quads, boolean useAmbientOcclusion, Sprite particleSprite) implements BlockModelPart {
   public GeometryBakedModel(BakedGeometry bakedGeometry, boolean bl, Sprite sprite) {
      this.quads = bakedGeometry;
      this.useAmbientOcclusion = bl;
      this.particleSprite = sprite;
   }

   public static GeometryBakedModel create(Baker baker, Identifier id, ModelBakeSettings bakeSettings) {
      BakedSimpleModel bakedSimpleModel = baker.getModel(id);
      ModelTextures modelTextures = bakedSimpleModel.getTextures();
      boolean bl = bakedSimpleModel.getAmbientOcclusion();
      Sprite sprite = bakedSimpleModel.getParticleTexture(modelTextures, baker);
      BakedGeometry bakedGeometry = bakedSimpleModel.bakeGeometry(modelTextures, baker, bakeSettings);
      return new GeometryBakedModel(bakedGeometry, bl, sprite);
   }

   public List getQuads(@Nullable Direction side) {
      return this.quads.getQuads(side);
   }

   public BakedGeometry quads() {
      return this.quads;
   }

   public boolean useAmbientOcclusion() {
      return this.useAmbientOcclusion;
   }

   public Sprite particleSprite() {
      return this.particleSprite;
   }
}
