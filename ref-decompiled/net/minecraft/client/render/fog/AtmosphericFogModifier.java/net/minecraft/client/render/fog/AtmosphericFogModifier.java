/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraOverride;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.biome.Biome;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AtmosphericFogModifier
extends FogModifier {
    private static final int field_60795 = 8;
    private static final float field_60587 = -160.0f;
    private static final float field_60588 = -256.0f;
    private float fogMultiplier;

    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        float g;
        int i = camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.FOG_COLOR_VISUAL, skyDarkness);
        if (viewDistance >= 4) {
            int j;
            float k;
            float f = camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.SUN_ANGLE_VISUAL, skyDarkness).floatValue() * ((float)Math.PI / 180);
            g = MathHelper.sin(f) > 0.0f ? -1.0f : 1.0f;
            CameraOverride cameraOverride = MinecraftClient.getInstance().gameRenderer.getCameraOverride();
            Vector3fc vector3fc = cameraOverride != null ? cameraOverride.forwardVector() : camera.getHorizontalPlane();
            float h = vector3fc.dot(g, 0.0f, 0.0f);
            if (h > 0.0f && (k = ColorHelper.getAlphaFloat(j = camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.SUNRISE_SUNSET_COLOR_VISUAL, skyDarkness).intValue())) > 0.0f) {
                i = ColorHelper.lerp(h * k, i, ColorHelper.fullAlpha(j));
            }
        }
        int l = camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.SKY_COLOR_VISUAL, skyDarkness);
        l = AtmosphericFogModifier.method_76556(l, world.getRainGradient(skyDarkness), world.getThunderGradient(skyDarkness));
        g = Math.min(camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.SKY_FOG_END_DISTANCE_VISUAL, skyDarkness).floatValue() / 16.0f, (float)viewDistance);
        float m = MathHelper.clampedLerp(g / 32.0f, 0.25f, 1.0f);
        m = 1.0f - (float)Math.pow(m, 0.25);
        i = ColorHelper.lerp(m, i, l);
        return i;
    }

    private static int method_76556(int i, float f, float g) {
        if (f > 0.0f) {
            float h = 1.0f - f * 0.5f;
            float j = 1.0f - f * 0.4f;
            i = ColorHelper.scaleRgb(i, h, h, j);
        }
        if (g > 0.0f) {
            i = ColorHelper.scaleRgb(i, 1.0f - g * 0.5f);
        }
        return i;
    }

    @Override
    public void applyStartEndModifier(FogData data, Camera camera, ClientWorld clientWorld, float f, RenderTickCounter renderTickCounter) {
        this.method_76304(camera, clientWorld, renderTickCounter);
        float g = renderTickCounter.getTickProgress(false);
        data.environmentalStart = camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.FOG_START_DISTANCE_VISUAL, g).floatValue();
        data.environmentalEnd = camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.FOG_END_DISTANCE_VISUAL, g).floatValue();
        data.environmentalStart += -160.0f * this.fogMultiplier;
        float h = Math.min(96.0f, data.environmentalEnd);
        data.environmentalEnd = Math.max(h, data.environmentalEnd + -256.0f * this.fogMultiplier);
        data.skyEnd = Math.min(f, camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.SKY_FOG_END_DISTANCE_VISUAL, g).floatValue());
        data.cloudEnd = Math.min((float)(MinecraftClient.getInstance().options.getCloudRenderDistance().getValue() * 16), camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.CLOUD_FOG_END_DISTANCE_VISUAL, g).floatValue());
        if (MinecraftClient.getInstance().inGameHud.getBossBarHud().shouldThickenFog()) {
            data.environmentalStart = Math.min(data.environmentalStart, 10.0f);
            data.skyEnd = data.environmentalEnd = Math.min(data.environmentalEnd, 96.0f);
            data.cloudEnd = data.environmentalEnd;
        }
    }

    private void method_76304(Camera camera, ClientWorld clientWorld, RenderTickCounter renderTickCounter) {
        BlockPos blockPos = camera.getBlockPos();
        Biome biome = clientWorld.getBiome(blockPos).value();
        float f = renderTickCounter.getDynamicDeltaTicks();
        float g = renderTickCounter.getTickProgress(false);
        boolean bl = biome.hasPrecipitation();
        float h = MathHelper.clamp(((float)clientWorld.getLightingProvider().get(LightType.SKY).getLightLevel(blockPos) - 8.0f) / 7.0f, 0.0f, 1.0f);
        float i = clientWorld.getRainGradient(g) * h * (bl ? 1.0f : 0.5f);
        this.fogMultiplier += (i - this.fogMultiplier) * f * 0.2f;
    }

    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        return submersionType == CameraSubmersionType.ATMOSPHERIC;
    }
}
