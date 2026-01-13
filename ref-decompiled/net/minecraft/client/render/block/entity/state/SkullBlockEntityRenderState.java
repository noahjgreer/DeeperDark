/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.SkullBlockEntityRenderState
 *  net.minecraft.util.math.Direction
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class SkullBlockEntityRenderState
extends BlockEntityRenderState {
    public float poweredTicks;
    public Direction facing = Direction.NORTH;
    public float yaw;
    public SkullBlock.SkullType skullType = SkullBlock.Type.ZOMBIE;
    public RenderLayer renderLayer;
}

