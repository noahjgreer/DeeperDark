/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.RabbitEntityRenderState
 *  net.minecraft.entity.passive.RabbitEntity$Variant
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.RabbitEntity;

@Environment(value=EnvType.CLIENT)
public class RabbitEntityRenderState
extends LivingEntityRenderState {
    public float jumpProgress;
    public boolean isToast;
    public RabbitEntity.Variant type = RabbitEntity.Variant.DEFAULT;
}

