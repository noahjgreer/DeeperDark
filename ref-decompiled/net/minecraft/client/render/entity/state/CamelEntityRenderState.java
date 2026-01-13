/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.CamelEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.AnimationState
 *  net.minecraft.item.ItemStack
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.AnimationState;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class CamelEntityRenderState
extends LivingEntityRenderState {
    public ItemStack saddleStack = ItemStack.EMPTY;
    public boolean hasPassengers;
    public float jumpCooldown;
    public final AnimationState sittingTransitionAnimationState = new AnimationState();
    public final AnimationState sittingAnimationState = new AnimationState();
    public final AnimationState standingTransitionAnimationState = new AnimationState();
    public final AnimationState idlingAnimationState = new AnimationState();
    public final AnimationState dashingAnimationState = new AnimationState();
}

