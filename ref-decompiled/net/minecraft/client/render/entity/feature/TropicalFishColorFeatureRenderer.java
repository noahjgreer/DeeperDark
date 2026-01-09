package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TropicalFishColorFeatureRenderer extends FeatureRenderer {
   private static final Identifier KOB_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_1.png");
   private static final Identifier SUNSTREAK_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_2.png");
   private static final Identifier SNOOPER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_3.png");
   private static final Identifier DASHER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_4.png");
   private static final Identifier BRINELY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_5.png");
   private static final Identifier SPOTTY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_6.png");
   private static final Identifier FLOPPER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_1.png");
   private static final Identifier STRIPEY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_2.png");
   private static final Identifier GLITTER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_3.png");
   private static final Identifier BLOCKFISH_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_4.png");
   private static final Identifier BETTY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_5.png");
   private static final Identifier CLAYFISH_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_6.png");
   private final SmallTropicalFishEntityModel smallModel;
   private final LargeTropicalFishEntityModel largeModel;

   public TropicalFishColorFeatureRenderer(FeatureRendererContext context, LoadedEntityModels loader) {
      super(context);
      this.smallModel = new SmallTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN));
      this.largeModel = new LargeTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN));
   }

   public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f, float g) {
      TropicalFishEntity.Pattern pattern = tropicalFishEntityRenderState.variety;
      Object var10000;
      switch (pattern.getSize()) {
         case SMALL:
            var10000 = this.smallModel;
            break;
         case LARGE:
            var10000 = this.largeModel;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      EntityModel entityModel = var10000;
      Identifier var10;
      switch (pattern) {
         case KOB:
            var10 = KOB_TEXTURE;
            break;
         case SUNSTREAK:
            var10 = SUNSTREAK_TEXTURE;
            break;
         case SNOOPER:
            var10 = SNOOPER_TEXTURE;
            break;
         case DASHER:
            var10 = DASHER_TEXTURE;
            break;
         case BRINELY:
            var10 = BRINELY_TEXTURE;
            break;
         case SPOTTY:
            var10 = SPOTTY_TEXTURE;
            break;
         case FLOPPER:
            var10 = FLOPPER_TEXTURE;
            break;
         case STRIPEY:
            var10 = STRIPEY_TEXTURE;
            break;
         case GLITTER:
            var10 = GLITTER_TEXTURE;
            break;
         case BLOCKFISH:
            var10 = BLOCKFISH_TEXTURE;
            break;
         case BETTY:
            var10 = BETTY_TEXTURE;
            break;
         case CLAYFISH:
            var10 = CLAYFISH_TEXTURE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Identifier identifier = var10;
      render((EntityModel)entityModel, identifier, matrixStack, vertexConsumerProvider, i, tropicalFishEntityRenderState, tropicalFishEntityRenderState.patternColor);
   }
}
