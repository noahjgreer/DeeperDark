package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer extends BipedEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

   protected ZombieBaseEntityRenderer(EntityRendererFactory.Context context, ZombieEntityModel mainModel, ZombieEntityModel babyMainModel, ZombieEntityModel armorInnerModel, ZombieEntityModel armorOuterModel, ZombieEntityModel babyArmorInnerModel, ZombieEntityModel babyArmorOuterModel) {
      super(context, mainModel, babyMainModel, 0.5F);
      this.addFeature(new ArmorFeatureRenderer(this, armorInnerModel, armorOuterModel, babyArmorInnerModel, babyArmorOuterModel, context.getEquipmentRenderer()));
   }

   public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
      return TEXTURE;
   }

   public void updateRenderState(ZombieEntity zombieEntity, ZombieEntityRenderState zombieEntityRenderState, float f) {
      super.updateRenderState((MobEntity)zombieEntity, (BipedEntityRenderState)zombieEntityRenderState, f);
      zombieEntityRenderState.attacking = zombieEntity.isAttacking();
      zombieEntityRenderState.convertingInWater = zombieEntity.isConvertingInWater();
   }

   protected boolean isShaking(ZombieEntityRenderState zombieEntityRenderState) {
      return super.isShaking(zombieEntityRenderState) || zombieEntityRenderState.convertingInWater;
   }

   // $FF: synthetic method
   protected boolean isShaking(final LivingEntityRenderState state) {
      return this.isShaking((ZombieEntityRenderState)state);
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((ZombieEntityRenderState)state);
   }
}
