package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class LavaFogModifier extends FogModifier {
   private static final int COLOR = -6743808;

   public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
      return -6743808;
   }

   public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
      if (cameraEntity.isSpectator()) {
         data.environmentalStart = -8.0F;
         data.environmentalEnd = viewDistance * 0.5F;
      } else {
         label14: {
            if (cameraEntity instanceof LivingEntity) {
               LivingEntity livingEntity = (LivingEntity)cameraEntity;
               if (livingEntity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                  data.environmentalStart = 0.0F;
                  data.environmentalEnd = 5.0F;
                  break label14;
               }
            }

            data.environmentalStart = 0.25F;
            data.environmentalEnd = 1.0F;
         }
      }

      data.skyEnd = data.environmentalEnd;
      data.cloudEnd = data.environmentalEnd;
   }

   public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
      return submersionType == CameraSubmersionType.LAVA;
   }
}
