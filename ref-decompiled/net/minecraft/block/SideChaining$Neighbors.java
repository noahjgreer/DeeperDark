/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideChaining;
import net.minecraft.block.enums.SideChainPart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public record SideChaining.Neighbors(SideChaining block, WorldAccess world, Direction facing, BlockPos center, Map<BlockPos, SideChaining.Neighbor> cache) {
    private boolean canChainWith(BlockState state) {
        return this.block.canChainWith(state) && this.block.getFacing(state) == this.facing;
    }

    private SideChaining.Neighbor createNeighbor(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);
        SideChainPart sideChainPart = this.canChainWith(blockState) ? this.block.getSideChainPart(blockState) : null;
        return sideChainPart == null ? new SideChaining.EmptyNeighbor(pos) : new SideChaining.SideChainNeighbor(this.world, this.block, pos, sideChainPart);
    }

    private SideChaining.Neighbor getOrCreateNeighbor(Direction direction, Integer distance) {
        return this.cache.computeIfAbsent(this.center.offset(direction, (int)distance), this::createNeighbor);
    }

    public SideChaining.Neighbor getLeftNeighbor(int distance) {
        return this.getOrCreateNeighbor(this.facing.rotateYClockwise(), distance);
    }

    public SideChaining.Neighbor getRightNeighbor(int distance) {
        return this.getOrCreateNeighbor(this.facing.rotateYCounterclockwise(), distance);
    }

    public SideChaining.Neighbor getLeftNeighbor() {
        return this.getLeftNeighbor(1);
    }

    public SideChaining.Neighbor getRightNeighbor() {
        return this.getRightNeighbor(1);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SideChaining.Neighbors.class, "block;level;facing;center;cache", "block", "world", "facing", "center", "cache"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SideChaining.Neighbors.class, "block;level;facing;center;cache", "block", "world", "facing", "center", "cache"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SideChaining.Neighbors.class, "block;level;facing;center;cache", "block", "world", "facing", "center", "cache"}, this, object);
    }
}
