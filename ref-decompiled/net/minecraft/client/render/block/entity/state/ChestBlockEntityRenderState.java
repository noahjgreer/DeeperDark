/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.enums.ChestType
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState$Variant
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.ChestBlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderState
extends BlockEntityRenderState {
    public ChestType chestType = ChestType.SINGLE;
    public float lidAnimationProgress;
    public float yaw;
    public Variant variant = Variant.REGULAR;
}

