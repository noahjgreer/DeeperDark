package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PufferfishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PufferfishEntityRenderer extends MobEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/pufferfish.png");
   private final EntityModel smallModel;
   private final EntityModel mediumModel;
   private final EntityModel largeModel = this.getModel();

   public PufferfishEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new LargePufferfishEntityModel(context.getPart(EntityModelLayers.PUFFERFISH_BIG)), 0.2F);
      this.mediumModel = new MediumPufferfishEntityModel(context.getPart(EntityModelLayers.PUFFERFISH_MEDIUM));
      this.smallModel = new SmallPufferfishEntityModel(context.getPart(EntityModelLayers.PUFFERFISH_SMALL));
   }

   public Identifier getTexture(PufferfishEntityRenderState pufferfishEntityRenderState) {
      return TEXTURE;
   }

   public PufferfishEntityRenderState createRenderState() {
      return new PufferfishEntityRenderState();
   }

   protected float getShadowRadius(PufferfishEntityRenderState pufferfishEntityRenderState) {
      return 0.1F + 0.1F * (float)pufferfishEntityRenderState.puffState;
   }

   public void render(PufferfishEntityRenderState pufferfishEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      EntityModel var10001;
      switch (pufferfishEntityRenderState.puffState) {
         case 0:
            var10001 = this.smallModel;
            break;
         case 1:
            var10001 = this.mediumModel;
            break;
         default:
            var10001 = this.largeModel;
      }

      this.model = var10001;
      super.render(pufferfishEntityRenderState, matrixStack, vertexConsumerProvider, i);
   }

   public void updateRenderState(PufferfishEntity pufferfishEntity, PufferfishEntityRenderState pufferfishEntityRenderState, float f) {
      super.updateRenderState(pufferfishEntity, pufferfishEntityRenderState, f);
      pufferfishEntityRenderState.puffState = pufferfishEntity.getPuffState();
   }

   protected void setupTransforms(PufferfishEntityRenderState pufferfishEntityRenderState, MatrixStack matrixStack, float f, float g) {
      matrixStack.translate(0.0F, MathHelper.cos(pufferfishEntityRenderState.age * 0.05F) * 0.08F, 0.0F);
      super.setupTransforms(pufferfishEntityRenderState, matrixStack, f, g);
   }

   // $FF: synthetic method
   protected float getShadowRadius(final LivingEntityRenderState livingEntityRenderState) {
      return this.getShadowRadius((PufferfishEntityRenderState)livingEntityRenderState);
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((PufferfishEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState state) {
      return this.getShadowRadius((PufferfishEntityRenderState)state);
   }
}
