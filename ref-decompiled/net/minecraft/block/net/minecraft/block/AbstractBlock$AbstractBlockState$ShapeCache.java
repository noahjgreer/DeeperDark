/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Arrays;
import java.util.Locale;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.EmptyBlockView;

static final class AbstractBlock.AbstractBlockState.ShapeCache {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int SHAPE_TYPE_LENGTH = SideShapeType.values().length;
    protected final VoxelShape collisionShape;
    protected final boolean exceedsCube;
    private final boolean[] solidSides;
    protected final boolean isFullCube;

    AbstractBlock.AbstractBlockState.ShapeCache(BlockState state) {
        Block block = state.getBlock();
        this.collisionShape = block.getCollisionShape(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, ShapeContext.absent());
        if (!this.collisionShape.isEmpty() && state.hasModelOffset()) {
            throw new IllegalStateException(String.format(Locale.ROOT, "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.", Registries.BLOCK.getId(block)));
        }
        this.exceedsCube = Arrays.stream(Direction.Axis.values()).anyMatch(axis -> this.collisionShape.getMin((Direction.Axis)axis) < 0.0 || this.collisionShape.getMax((Direction.Axis)axis) > 1.0);
        this.solidSides = new boolean[DIRECTIONS.length * SHAPE_TYPE_LENGTH];
        for (Direction direction : DIRECTIONS) {
            for (SideShapeType sideShapeType : SideShapeType.values()) {
                this.solidSides[AbstractBlock.AbstractBlockState.ShapeCache.indexSolidSide((Direction)direction, (SideShapeType)sideShapeType)] = sideShapeType.matches(state, EmptyBlockView.INSTANCE, BlockPos.ORIGIN, direction);
            }
        }
        this.isFullCube = Block.isShapeFullCube(state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN));
    }

    public boolean isSideSolid(Direction direction, SideShapeType shapeType) {
        return this.solidSides[AbstractBlock.AbstractBlockState.ShapeCache.indexSolidSide(direction, shapeType)];
    }

    private static int indexSolidSide(Direction direction, SideShapeType shapeType) {
        return direction.ordinal() * SHAPE_TYPE_LENGTH + shapeType.ordinal();
    }
}
