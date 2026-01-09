package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EndermanEyesFeatureRenderer extends EyesFeatureRenderer {
   private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.ofVanilla("textures/entity/enderman/enderman_eyes.png"));

   public EndermanEyesFeatureRenderer(FeatureRendererContext featureRendererContext) {
      super(featureRendererContext);
   }

   public RenderLayer getEyesTexture() {
      return SKIN;
   }
}
