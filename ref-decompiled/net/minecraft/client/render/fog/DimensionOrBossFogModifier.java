package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DimensionOrBossFogModifier extends StandardFogModifier {
   public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
      data.environmentalStart = viewDistance * 0.05F;
      data.environmentalEnd = Math.min(viewDistance, 192.0F) * 0.5F;
      data.skyEnd = data.environmentalEnd;
      data.cloudEnd = data.environmentalEnd;
   }

   public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
      return submersionType == CameraSubmersionType.DIMENSION_OR_BOSS;
   }
}
