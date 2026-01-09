package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class WaterFogModifier extends FogModifier {
   private static final int field_60592 = 96;
   private static final float field_60593 = 5000.0F;
   private static int waterFogColor = -1;
   private static int lerpedWaterFogColor = -1;
   private static long updateTime = -1L;

   public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
      data.environmentalStart = -8.0F;
      data.environmentalEnd = 96.0F;
      if (cameraEntity instanceof ClientPlayerEntity clientPlayerEntity) {
         data.environmentalEnd *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
         if (world.getBiome(cameraPos).isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
            data.environmentalEnd *= 0.85F;
         }
      }

      data.skyEnd = data.environmentalEnd;
      data.cloudEnd = data.environmentalEnd;
   }

   public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
      return submersionType == CameraSubmersionType.WATER;
   }

   public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
      long l = Util.getMeasuringTimeMs();
      int i = ((Biome)world.getBiome(camera.getBlockPos()).value()).getWaterFogColor();
      if (updateTime < 0L) {
         waterFogColor = i;
         lerpedWaterFogColor = i;
         updateTime = l;
      }

      float f = MathHelper.clamp((float)(l - updateTime) / 5000.0F, 0.0F, 1.0F);
      int j = ColorHelper.lerp(f, lerpedWaterFogColor, waterFogColor);
      if (waterFogColor != i) {
         waterFogColor = i;
         lerpedWaterFogColor = j;
         updateTime = l;
      }

      return j;
   }

   public void onSkipped() {
      updateTime = -1L;
   }
}
