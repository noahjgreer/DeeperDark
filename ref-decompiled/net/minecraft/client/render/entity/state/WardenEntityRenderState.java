/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.WardenEntityRenderState
 *  net.minecraft.entity.AnimationState
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;

@Environment(value=EnvType.CLIENT)
public class WardenEntityRenderState
extends LivingEntityRenderState {
    public float tendrilAlpha;
    public float heartAlpha;
    public final AnimationState roaringAnimationState = new AnimationState();
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState emergingAnimationState = new AnimationState();
    public final AnimationState diggingAnimationState = new AnimationState();
    public final AnimationState attackingAnimationState = new AnimationState();
    public final AnimationState chargingSonicBoomAnimationState = new AnimationState();
}

