/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.CopperGolemStatueBlock$Pose
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.CopperGolemStatueBlockEntityRenderState
 *  net.minecraft.util.math.Direction
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class CopperGolemStatueBlockEntityRenderState
extends BlockEntityRenderState {
    public CopperGolemStatueBlock.Pose pose = CopperGolemStatueBlock.Pose.STANDING;
    public Direction facing = Direction.NORTH;
}

