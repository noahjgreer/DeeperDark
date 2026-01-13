/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.shape.VoxelShape;

@Environment(value=EnvType.CLIENT)
public record EntityRenderState.ShadowPiece(float relativeX, float relativeY, float relativeZ, VoxelShape shapeBelow, float alpha) {
}
