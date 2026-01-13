/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.enums.CameraSubmersionType
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.fog.FogData
 *  net.minecraft.client.render.fog.FogModifier
 *  net.minecraft.client.render.fog.WaterFogModifier
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WaterFogModifier
extends FogModifier {
    public void applyStartEndModifier(FogData data, Camera camera, ClientWorld clientWorld, float f, RenderTickCounter renderTickCounter) {
        float g = renderTickCounter.getTickProgress(false);
        data.environmentalStart = ((Float)camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.WATER_FOG_START_DISTANCE_VISUAL, g)).floatValue();
        data.environmentalEnd = ((Float)camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.WATER_FOG_END_DISTANCE_VISUAL, g)).floatValue();
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
            data.environmentalEnd *= Math.max(0.25f, clientPlayerEntity.getUnderwaterVisibility());
        }
        data.skyEnd = data.environmentalEnd;
        data.cloudEnd = data.environmentalEnd;
    }

    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        return submersionType == CameraSubmersionType.WATER;
    }

    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        return (Integer)camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.WATER_FOG_COLOR_VISUAL, skyDarkness);
    }
}

