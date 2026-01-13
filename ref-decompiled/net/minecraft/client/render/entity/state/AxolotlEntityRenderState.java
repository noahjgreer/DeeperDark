/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.AxolotlEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.passive.AxolotlEntity$Variant
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.AxolotlEntity;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityRenderState
extends LivingEntityRenderState {
    public AxolotlEntity.Variant variant = AxolotlEntity.Variant.DEFAULT;
    public float playingDeadValue;
    public float isMovingValue;
    public float inWaterValue = 1.0f;
    public float onGroundValue;
}

