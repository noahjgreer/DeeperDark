/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.enums.CameraSubmersionType
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.fog.FogData
 *  net.minecraft.client.render.fog.FogModifier
 *  net.minecraft.client.render.fog.LavaFogModifier
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LavaFogModifier
extends FogModifier {
    private static final int COLOR = -6743808;

    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        return -6743808;
    }

    public void applyStartEndModifier(FogData data, Camera camera, ClientWorld clientWorld, float f, RenderTickCounter renderTickCounter) {
        if (camera.getFocusedEntity().isSpectator()) {
            data.environmentalStart = -8.0f;
            data.environmentalEnd = f * 0.5f;
        } else {
            LivingEntity livingEntity;
            Entity entity = camera.getFocusedEntity();
            if (entity instanceof LivingEntity && (livingEntity = (LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                data.environmentalStart = 0.0f;
                data.environmentalEnd = 5.0f;
            } else {
                data.environmentalStart = 0.25f;
                data.environmentalEnd = 1.0f;
            }
        }
        data.skyEnd = data.environmentalEnd;
        data.cloudEnd = data.environmentalEnd;
    }

    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        return submersionType == CameraSubmersionType.LAVA;
    }
}

