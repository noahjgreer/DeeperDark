/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

@Environment(value=EnvType.CLIENT)
public class BreezeEntityRenderState
extends LivingEntityRenderState {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState shootingAnimationState = new AnimationState();
    public final AnimationState slidingAnimationState = new AnimationState();
    public final AnimationState slidingBackAnimationState = new AnimationState();
    public final AnimationState inhalingAnimationState = new AnimationState();
    public final AnimationState longJumpingAnimationState = new AnimationState();
}
