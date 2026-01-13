/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class BedBlockEntityRenderState
extends BlockEntityRenderState {
    public DyeColor dyeColor = DyeColor.WHITE;
    public Direction facing = Direction.NORTH;
    public boolean headPart;
}
