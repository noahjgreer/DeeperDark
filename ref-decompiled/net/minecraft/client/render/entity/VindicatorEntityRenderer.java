package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class VindicatorEntityRenderer extends IllagerEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/vindicator.png");

   public VindicatorEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.VINDICATOR)), 0.5F);
      this.addFeature(new HeldItemFeatureRenderer(this, this) {
         public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, IllagerEntityRenderState illagerEntityRenderState, float f, float g) {
            if (illagerEntityRenderState.attacking) {
               super.render(matrixStack, vertexConsumerProvider, i, (ArmedEntityRenderState)illagerEntityRenderState, f, g);
            }

         }
      });
   }

   public Identifier getTexture(IllagerEntityRenderState illagerEntityRenderState) {
      return TEXTURE;
   }

   public IllagerEntityRenderState createRenderState() {
      return new IllagerEntityRenderState();
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((IllagerEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
