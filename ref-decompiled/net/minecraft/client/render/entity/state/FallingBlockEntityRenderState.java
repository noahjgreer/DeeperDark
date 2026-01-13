/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.block.MovingBlockRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.FallingBlockEntityRenderState
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;

@Environment(value=EnvType.CLIENT)
public class FallingBlockEntityRenderState
extends EntityRenderState {
    public MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
}

