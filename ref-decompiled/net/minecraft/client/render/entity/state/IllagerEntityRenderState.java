/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.IllagerEntityRenderState
 *  net.minecraft.client.render.entity.state.LancerEntityRenderState
 *  net.minecraft.entity.mob.IllagerEntity$State
 *  net.minecraft.util.Arm
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LancerEntityRenderState;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class IllagerEntityRenderState
extends LancerEntityRenderState {
    public boolean hasVehicle;
    public boolean attacking;
    public Arm illagerMainArm = Arm.RIGHT;
    public IllagerEntity.State illagerState = IllagerEntity.State.NEUTRAL;
    public int crossbowPullTime;
    public float itemUseTime;
    public float handSwingProgress;
}

