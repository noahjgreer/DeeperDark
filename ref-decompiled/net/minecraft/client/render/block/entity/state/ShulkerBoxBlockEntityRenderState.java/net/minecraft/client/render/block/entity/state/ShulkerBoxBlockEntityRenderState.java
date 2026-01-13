/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxBlockEntityRenderState
extends BlockEntityRenderState {
    public Direction facing = Direction.NORTH;
    public @Nullable DyeColor dyeColor;
    public float animationProgress;
}
