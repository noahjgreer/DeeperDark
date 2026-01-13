/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.ArmadilloEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.AnimationState
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

@Environment(value=EnvType.CLIENT)
public class ArmadilloEntityRenderState
extends LivingEntityRenderState {
    public boolean rolledUp;
    public final AnimationState unrollingAnimationState = new AnimationState();
    public final AnimationState rollingAnimationState = new AnimationState();
    public final AnimationState scaredAnimationState = new AnimationState();
}

