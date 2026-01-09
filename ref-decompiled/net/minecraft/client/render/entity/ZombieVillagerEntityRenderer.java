package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieVillagerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieVillagerRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieVillagerEntityRenderer extends BipedEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER)), new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY)), 0.5F, VillagerEntityRenderer.HEAD_TRANSFORMATION);
      this.addFeature(new ArmorFeatureRenderer(this, new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_INNER_ARMOR)), new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_OUTER_ARMOR)), new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY_INNER_ARMOR)), new ZombieVillagerEntityModel(context.getPart(EntityModelLayers.ZOMBIE_VILLAGER_BABY_OUTER_ARMOR)), context.getEquipmentRenderer()));
      this.addFeature(new VillagerClothingFeatureRenderer(this, context.getResourceManager(), "zombie_villager"));
   }

   public Identifier getTexture(ZombieVillagerRenderState zombieVillagerRenderState) {
      return TEXTURE;
   }

   public ZombieVillagerRenderState createRenderState() {
      return new ZombieVillagerRenderState();
   }

   public void updateRenderState(ZombieVillagerEntity zombieVillagerEntity, ZombieVillagerRenderState zombieVillagerRenderState, float f) {
      super.updateRenderState((MobEntity)zombieVillagerEntity, (BipedEntityRenderState)zombieVillagerRenderState, f);
      zombieVillagerRenderState.convertingInWater = zombieVillagerEntity.isConverting();
      zombieVillagerRenderState.villagerData = zombieVillagerEntity.getVillagerData();
      zombieVillagerRenderState.attacking = zombieVillagerEntity.isAttacking();
   }

   protected boolean isShaking(ZombieVillagerRenderState zombieVillagerRenderState) {
      return super.isShaking(zombieVillagerRenderState) || zombieVillagerRenderState.convertingInWater;
   }

   // $FF: synthetic method
   protected boolean isShaking(final LivingEntityRenderState state) {
      return this.isShaking((ZombieVillagerRenderState)state);
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((ZombieVillagerRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
