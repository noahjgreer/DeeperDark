package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SilverfishEntityRenderer extends MobEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/silverfish.png");

   public SilverfishEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new SilverfishEntityModel(context.getPart(EntityModelLayers.SILVERFISH)), 0.3F);
   }

   protected float getLyingPositionRotationDegrees() {
      return 180.0F;
   }

   public Identifier getTexture(LivingEntityRenderState state) {
      return TEXTURE;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
