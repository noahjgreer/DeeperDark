package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.AxolotlEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.AxolotlEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class AxolotlEntityRenderer extends AgeableMobEntityRenderer {
   private static final Map TEXTURES = (Map)Util.make(Maps.newHashMap(), (variants) -> {
      AxolotlEntity.Variant[] var1 = AxolotlEntity.Variant.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         AxolotlEntity.Variant variant = var1[var3];
         variants.put(variant, Identifier.ofVanilla(String.format(Locale.ROOT, "textures/entity/axolotl/axolotl_%s.png", variant.getId())));
      }

   });

   public AxolotlEntityRenderer(EntityRendererFactory.Context context) {
      super(context, new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL)), new AxolotlEntityModel(context.getPart(EntityModelLayers.AXOLOTL_BABY)), 0.5F);
   }

   public Identifier getTexture(AxolotlEntityRenderState axolotlEntityRenderState) {
      return (Identifier)TEXTURES.get(axolotlEntityRenderState.variant);
   }

   public AxolotlEntityRenderState createRenderState() {
      return new AxolotlEntityRenderState();
   }

   public void updateRenderState(AxolotlEntity axolotlEntity, AxolotlEntityRenderState axolotlEntityRenderState, float f) {
      super.updateRenderState(axolotlEntity, axolotlEntityRenderState, f);
      axolotlEntityRenderState.variant = axolotlEntity.getVariant();
      axolotlEntityRenderState.playingDeadValue = axolotlEntity.playingDeadFf.getValue(f);
      axolotlEntityRenderState.inWaterValue = axolotlEntity.inWaterFf.getValue(f);
      axolotlEntityRenderState.onGroundValue = axolotlEntity.onGroundFf.getValue(f);
      axolotlEntityRenderState.isMovingValue = axolotlEntity.isMovingFf.getValue(f);
   }

   // $FF: synthetic method
   public Identifier getTexture(final LivingEntityRenderState state) {
      return this.getTexture((AxolotlEntityRenderState)state);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
