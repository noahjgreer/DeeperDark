package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BlindnessEffectFogModifier extends StatusEffectFogModifier {
   public RegistryEntry getStatusEffect() {
      return StatusEffects.BLINDNESS;
   }

   public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
      if (cameraEntity instanceof LivingEntity livingEntity) {
         StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(this.getStatusEffect());
         if (statusEffectInstance != null) {
            float f = statusEffectInstance.isInfinite() ? 5.0F : MathHelper.lerp(Math.min(1.0F, (float)statusEffectInstance.getDuration() / 20.0F), viewDistance, 5.0F);
            data.environmentalStart = f * 0.25F;
            data.environmentalEnd = f;
            data.skyEnd = f * 0.8F;
            data.cloudEnd = f * 0.8F;
         }
      }

   }

   public float applyDarknessModifier(LivingEntity cameraEntity, float darkness, float tickProgress) {
      StatusEffectInstance statusEffectInstance = cameraEntity.getStatusEffect(this.getStatusEffect());
      if (statusEffectInstance != null) {
         if (statusEffectInstance.isDurationBelow(19)) {
            darkness = Math.max((float)statusEffectInstance.getDuration() / 20.0F, darkness);
         } else {
            darkness = 1.0F;
         }
      }

      return darkness;
   }
}
