/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.Sherds
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.DecoratedPotBlockEntityRenderState
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DecoratedPotBlockEntityRenderState
extends BlockEntityRenderState {
    public float field_62713;
    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable DecoratedPotBlockEntity.WobbleType wobbleType;
    public float wobbleAnimationProgress;
    public Sherds sherds = Sherds.DEFAULT;
    public Direction facing = Direction.NORTH;
}

