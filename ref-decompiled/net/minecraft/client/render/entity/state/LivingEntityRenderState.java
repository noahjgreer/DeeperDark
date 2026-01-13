/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.component.type.ProfileComponent
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LivingEntityRenderState
extends EntityRenderState {
    public float bodyYaw;
    public float relativeHeadYaw;
    public float pitch;
    public float deathTime;
    public float limbSwingAnimationProgress;
    public float limbSwingAmplitude;
    public float baseScale = 1.0f;
    public float ageScale = 1.0f;
    public float timeSinceLastKineticAttack;
    public boolean flipUpsideDown;
    public boolean shaking;
    public boolean baby;
    public boolean touchingWater;
    public boolean usingRiptide;
    public boolean hurt;
    public boolean invisibleToPlayer;
    public @Nullable Direction sleepingDirection;
    public EntityPose pose = EntityPose.STANDING;
    public final ItemRenderState headItemRenderState = new ItemRenderState();
    public float headItemAnimationProgress;
    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable SkullBlock.SkullType wearingSkullType;
    public @Nullable ProfileComponent wearingSkullProfile;

    public boolean isInPose(EntityPose pose) {
        return this.pose == pose;
    }
}

