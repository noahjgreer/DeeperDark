/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.enums.CameraSubmersionType
 *  net.minecraft.client.render.fog.FogModifier
 *  net.minecraft.client.render.fog.StatusEffectFogModifier
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.registry.entry.RegistryEntry
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.fog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class StatusEffectFogModifier
extends FogModifier {
    public abstract RegistryEntry<StatusEffect> getStatusEffect();

    public boolean isColorSource() {
        return false;
    }

    public boolean isDarknessModifier() {
        return true;
    }

    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        LivingEntity livingEntity;
        return cameraEntity instanceof LivingEntity && (livingEntity = (LivingEntity)cameraEntity).hasStatusEffect(this.getStatusEffect());
    }
}

