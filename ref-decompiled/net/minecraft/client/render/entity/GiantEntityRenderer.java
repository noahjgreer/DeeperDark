package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GiantEntityRenderer extends MobEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

   public GiantEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
      super(ctx, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT)), 0.5F * scale);
      this.addFeature(new HeldItemFeatureRenderer(this));
      this.addFeature(new ArmorFeatureRenderer(this, new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_INNER_ARMOR)), new GiantEntityModel(ctx.getPart(EntityModelLayers.GIANT_OUTER_ARMOR)), ctx.getEquipmentRenderer()));
   }

   public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
      return TEXTURE;
   }

   public ZombieEntityRenderState createRenderState() {
      return new ZombieEntityRenderState();
   }

   public void updateRenderState(GiantEntity giantEntity, ZombieEntityRenderState zombieEntityRenderState, float f) {
      super.updateRenderState(giantEntity, zombieEntityRenderState, f);
      BipedEntityRenderer.updateBipedRenderState(giantEntity, zombieEntityRenderState, f, this.itemModelResolver);
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((ZombieEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
