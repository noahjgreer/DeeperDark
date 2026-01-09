package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.SalmonEntity;

@Environment(EnvType.CLIENT)
public class SalmonEntityRenderState extends LivingEntityRenderState {
   public SalmonEntity.Variant variant;

   public SalmonEntityRenderState() {
      this.variant = SalmonEntity.Variant.MEDIUM;
   }
}
