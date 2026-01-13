/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record OutlineRenderState(BlockPos pos, boolean isTranslucent, boolean highContrast, VoxelShape shape, @Nullable VoxelShape collisionShape, @Nullable VoxelShape occlusionShape, @Nullable VoxelShape interactionShape) implements FabricRenderState
{
    public OutlineRenderState(BlockPos pos, boolean isTranslucent, boolean highContrast, VoxelShape shape) {
        this(pos, isTranslucent, highContrast, shape, null, null, null);
    }
}
