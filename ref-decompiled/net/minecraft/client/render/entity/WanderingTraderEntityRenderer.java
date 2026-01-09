package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WanderingTraderEntityRenderer extends MobEntityRenderer {
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/wandering_trader.png");

   public WanderingTraderEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new VillagerResemblingModel(context.getPart(EntityModelLayers.WANDERING_TRADER)), 0.5F);
      this.addFeature(new HeadFeatureRenderer(this, context.getEntityModels()));
      this.addFeature(new VillagerHeldItemFeatureRenderer(this));
   }

   public Identifier getTexture(VillagerEntityRenderState villagerEntityRenderState) {
      return TEXTURE;
   }

   public VillagerEntityRenderState createRenderState() {
      return new VillagerEntityRenderState();
   }

   public void updateRenderState(WanderingTraderEntity wanderingTraderEntity, VillagerEntityRenderState villagerEntityRenderState, float f) {
      super.updateRenderState(wanderingTraderEntity, villagerEntityRenderState, f);
      ItemHolderEntityRenderState.update(wanderingTraderEntity, villagerEntityRenderState, this.itemModelResolver);
      villagerEntityRenderState.headRolling = wanderingTraderEntity.getHeadRollingTimeLeft() > 0;
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((VillagerEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
