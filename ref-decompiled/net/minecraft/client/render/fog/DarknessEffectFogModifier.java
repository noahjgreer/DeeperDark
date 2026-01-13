/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.fog.DarknessEffectFogModifier
 *  net.minecraft.client.render.fog.FogData
 *  net.minecraft.client.render.fog.StatusEffectFogModifier
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.StatusEffectFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DarknessEffectFogModifier
extends StatusEffectFogModifier {
    public RegistryEntry<StatusEffect> getStatusEffect() {
        return StatusEffects.DARKNESS;
    }

    public void applyStartEndModifier(FogData data, Camera camera, ClientWorld clientWorld, float f, RenderTickCounter renderTickCounter) {
        LivingEntity livingEntity;
        StatusEffectInstance statusEffectInstance;
        Entity entity = camera.getFocusedEntity();
        if (entity instanceof LivingEntity && (statusEffectInstance = (livingEntity = (LivingEntity)entity).getStatusEffect(this.getStatusEffect())) != null) {
            float g = MathHelper.lerp((float)statusEffectInstance.getFadeFactor(livingEntity, renderTickCounter.getTickProgress(false)), (float)f, (float)15.0f);
            data.environmentalStart = g * 0.75f;
            data.environmentalEnd = g;
            data.skyEnd = g;
            data.cloudEnd = g;
        }
    }

    public float applyDarknessModifier(LivingEntity cameraEntity, float darkness, float tickProgress) {
        StatusEffectInstance statusEffectInstance = cameraEntity.getStatusEffect(this.getStatusEffect());
        return statusEffectInstance != null ? Math.max(statusEffectInstance.getFadeFactor(cameraEntity, tickProgress), darkness) : darkness;
    }
}

