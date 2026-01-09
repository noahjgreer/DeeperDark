package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class StatusEffectFogModifier extends FogModifier {
   public abstract RegistryEntry getStatusEffect();

   public boolean isColorSource() {
      return false;
   }

   public boolean isDarknessModifier() {
      return true;
   }

   public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
      boolean var10000;
      if (cameraEntity instanceof LivingEntity livingEntity) {
         if (livingEntity.hasStatusEffect(this.getStatusEffect())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }
}
