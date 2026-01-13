/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.ShulkerEntityRenderState
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShulkerEntityRenderState
extends LivingEntityRenderState {
    public Vec3d renderPositionOffset = Vec3d.ZERO;
    public @Nullable DyeColor color;
    public float openProgress;
    public float headYaw;
    public float shellYaw;
    public Direction facing = Direction.DOWN;
}

