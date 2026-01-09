package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.AxolotlEntity;

@Environment(EnvType.CLIENT)
public class AxolotlEntityRenderState extends LivingEntityRenderState {
   public AxolotlEntity.Variant variant;
   public float playingDeadValue;
   public float isMovingValue;
   public float inWaterValue;
   public float onGroundValue;

   public AxolotlEntityRenderState() {
      this.variant = AxolotlEntity.Variant.DEFAULT;
      this.inWaterValue = 1.0F;
   }
}
