package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class AtmosphericFogModifier extends StandardFogModifier {
   private static final int field_60795 = 8;
   private static final float field_60587 = -160.0F;
   private static final float field_60588 = -256.0F;
   private float fogMultiplier;

   public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
      Biome biome = (Biome)world.getBiome(cameraPos).value();
      float f = tickCounter.getDynamicDeltaTicks();
      boolean bl = biome.hasPrecipitation();
      float g = MathHelper.clamp(((float)world.getLightingProvider().get(LightType.SKY).getLightLevel(cameraPos) - 8.0F) / 7.0F, 0.0F, 1.0F);
      float h = world.getRainGradient(tickCounter.getTickProgress(false)) * g * (bl ? 1.0F : 0.5F);
      this.fogMultiplier += (h - this.fogMultiplier) * f * 0.2F;
      data.environmentalStart = this.fogMultiplier * -160.0F;
      data.environmentalEnd = 1024.0F + -256.0F * this.fogMultiplier;
      data.skyEnd = viewDistance;
      data.cloudEnd = (float)((Integer)MinecraftClient.getInstance().options.getCloudRenderDistance().getValue() * 16);
   }

   public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
      return submersionType == CameraSubmersionType.ATMOSPHERIC;
   }
}
