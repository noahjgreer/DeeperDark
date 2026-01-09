package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer extends MobEntityRenderer {
   private final EntityModel smallModel = this.getModel();
   private final EntityModel largeModel;
   private static final Identifier A_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a.png");
   private static final Identifier B_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b.png");

   public TropicalFishEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new SmallTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
      this.largeModel = new LargeTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_LARGE));
      this.addFeature(new TropicalFishColorFeatureRenderer(this, context.getEntityModels()));
   }

   public Identifier getTexture(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
      Identifier var10000;
      switch (tropicalFishEntityRenderState.variety.getSize()) {
         case SMALL:
            var10000 = A_TEXTURE;
            break;
         case LARGE:
            var10000 = B_TEXTURE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public TropicalFishEntityRenderState createRenderState() {
      return new TropicalFishEntityRenderState();
   }

   public void updateRenderState(TropicalFishEntity tropicalFishEntity, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f) {
      super.updateRenderState(tropicalFishEntity, tropicalFishEntityRenderState, f);
      tropicalFishEntityRenderState.variety = tropicalFishEntity.getVariety();
      tropicalFishEntityRenderState.baseColor = tropicalFishEntity.getBaseColor().getEntityColor();
      tropicalFishEntityRenderState.patternColor = tropicalFishEntity.getPatternColor().getEntityColor();
   }

   public void render(TropicalFishEntityRenderState tropicalFishEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      EntityModel var10001;
      switch (tropicalFishEntityRenderState.variety.getSize()) {
         case SMALL:
            var10001 = this.smallModel;
            break;
         case LARGE:
            var10001 = this.largeModel;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      this.model = var10001;
      super.render(tropicalFishEntityRenderState, matrixStack, vertexConsumerProvider, i);
   }

   protected int getMixColor(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
      return tropicalFishEntityRenderState.baseColor;
   }

   protected void setupTransforms(TropicalFishEntityRenderState tropicalFishEntityRenderState, MatrixStack matrixStack, float f, float g) {
      super.setupTransforms(tropicalFishEntityRenderState, matrixStack, f, g);
      float h = 4.3F * MathHelper.sin(0.6F * tropicalFishEntityRenderState.age);
      matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
      if (!tropicalFishEntityRenderState.touchingWater) {
         matrixStack.translate(0.2F, 0.1F, 0.0F);
         matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
      }

   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((TropicalFishEntityRenderState)state);
   }

   // $FF: synthetic method
   protected int getMixColor(final LivingEntityRenderState state) {
      return this.getMixColor((TropicalFishEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
