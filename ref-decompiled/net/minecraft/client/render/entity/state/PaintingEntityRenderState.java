/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.PaintingEntityRenderState
 *  net.minecraft.entity.decoration.painting.PaintingVariant
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PaintingEntityRenderState
extends EntityRenderState {
    public Direction facing = Direction.NORTH;
    public @Nullable PaintingVariant variant;
    public int[] lightmapCoordinates = new int[0];
}

