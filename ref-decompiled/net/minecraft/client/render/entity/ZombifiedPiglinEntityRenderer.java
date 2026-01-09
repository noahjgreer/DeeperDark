package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ZombifiedPiglinEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombifiedPiglinEntityRenderer extends BipedEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/piglin/zombified_piglin.png");

   public ZombifiedPiglinEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer mainLayer, EntityModelLayer babyMainLayer, EntityModelLayer armorInnerLayer, EntityModelLayer armorOuterLayer, EntityModelLayer babyArmorInnerLayer, EntityModelLayer babyArmorOuterLayer) {
      super(context, new ZombifiedPiglinEntityModel(context.getPart(mainLayer)), new ZombifiedPiglinEntityModel(context.getPart(babyMainLayer)), 0.5F, PiglinEntityRenderer.HEAD_TRANSFORMATION);
      this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(context.getPart(armorInnerLayer)), new ArmorEntityModel(context.getPart(armorOuterLayer)), new ArmorEntityModel(context.getPart(babyArmorInnerLayer)), new ArmorEntityModel(context.getPart(babyArmorOuterLayer)), context.getEquipmentRenderer()));
   }

   public Identifier getTexture(ZombifiedPiglinEntityRenderState zombifiedPiglinEntityRenderState) {
      return TEXTURE;
   }

   public ZombifiedPiglinEntityRenderState createRenderState() {
      return new ZombifiedPiglinEntityRenderState();
   }

   public void updateRenderState(ZombifiedPiglinEntity zombifiedPiglinEntity, ZombifiedPiglinEntityRenderState zombifiedPiglinEntityRenderState, float f) {
      super.updateRenderState((MobEntity)zombifiedPiglinEntity, (BipedEntityRenderState)zombifiedPiglinEntityRenderState, f);
      zombifiedPiglinEntityRenderState.attacking = zombifiedPiglinEntity.isAttacking();
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((ZombifiedPiglinEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
