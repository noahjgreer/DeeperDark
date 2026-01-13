/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.MooshroomEntityRenderState
 *  net.minecraft.entity.passive.MooshroomEntity$Variant
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.passive.MooshroomEntity;

@Environment(value=EnvType.CLIENT)
public class MooshroomEntityRenderState
extends LivingEntityRenderState {
    public MooshroomEntity.Variant type = MooshroomEntity.Variant.RED;
}

