/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState
 *  net.minecraft.client.render.state.OutlineRenderState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.shape.VoxelShape
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
    private final BlockPos pos;
    private final boolean isTranslucent;
    private final boolean highContrast;
    private final VoxelShape shape;
    private final @Nullable VoxelShape collisionShape;
    private final @Nullable VoxelShape occlusionShape;
    private final @Nullable VoxelShape interactionShape;

    public OutlineRenderState(BlockPos pos, boolean isTranslucent, boolean highContrast, VoxelShape shape) {
        this(pos, isTranslucent, highContrast, shape, null, null, null);
    }

    public OutlineRenderState(BlockPos pos, boolean isTranslucent, boolean highContrast, VoxelShape shape, @Nullable VoxelShape collisionShape, @Nullable VoxelShape occlusionShape, @Nullable VoxelShape interactionShape) {
        this.pos = pos;
        this.isTranslucent = isTranslucent;
        this.highContrast = highContrast;
        this.shape = shape;
        this.collisionShape = collisionShape;
        this.occlusionShape = occlusionShape;
        this.interactionShape = interactionShape;
    }

    public BlockPos pos() {
        return this.pos;
    }

    public boolean isTranslucent() {
        return this.isTranslucent;
    }

    public boolean highContrast() {
        return this.highContrast;
    }

    public VoxelShape shape() {
        return this.shape;
    }

    public @Nullable VoxelShape collisionShape() {
        return this.collisionShape;
    }

    public @Nullable VoxelShape occlusionShape() {
        return this.occlusionShape;
    }

    public @Nullable VoxelShape interactionShape() {
        return this.interactionShape;
    }
}

