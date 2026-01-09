package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.EvokerEntityRenderState;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvokerEntityRenderer extends IllagerEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/evoker.png");

   public EvokerEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.EVOKER)), 0.5F);
      this.addFeature(new HeldItemFeatureRenderer(this, this) {
         public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, EvokerEntityRenderState evokerEntityRenderState, float f, float g) {
            if (evokerEntityRenderState.spellcasting) {
               super.render(matrixStack, vertexConsumerProvider, i, (ArmedEntityRenderState)evokerEntityRenderState, f, g);
            }

         }
      });
   }

   public Identifier getTexture(EvokerEntityRenderState evokerEntityRenderState) {
      return TEXTURE;
   }

   public EvokerEntityRenderState createRenderState() {
      return new EvokerEntityRenderState();
   }

   public void updateRenderState(SpellcastingIllagerEntity spellcastingIllagerEntity, EvokerEntityRenderState evokerEntityRenderState, float f) {
      super.updateRenderState((IllagerEntity)spellcastingIllagerEntity, (IllagerEntityRenderState)evokerEntityRenderState, f);
      evokerEntityRenderState.spellcasting = spellcastingIllagerEntity.isSpellcasting();
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((EvokerEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
