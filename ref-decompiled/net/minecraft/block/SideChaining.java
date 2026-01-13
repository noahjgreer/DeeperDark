/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.SideChaining
 *  net.minecraft.block.SideChaining$Neighbor
 *  net.minecraft.block.SideChaining$Neighbors
 *  net.minecraft.block.enums.SideChainPart
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.WorldAccess
 */
package net.minecraft.block;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.SideChaining;
import net.minecraft.block.enums.SideChainPart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public interface SideChaining {
    public SideChainPart getSideChainPart(BlockState var1);

    public BlockState withSideChainPart(BlockState var1, SideChainPart var2);

    public Direction getFacing(BlockState var1);

    public boolean canChainWith(BlockState var1);

    public int getMaxSideChainLength();

    default public List<BlockPos> getPositionsInChain(WorldAccess world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (!this.canChainWith(blockState)) {
            return List.of();
        }
        Neighbors neighbors = this.getNeighbors(world, pos, this.getFacing(blockState));
        LinkedList<BlockPos> list = new LinkedList<BlockPos>();
        list.add(pos);
        this.forEachNeighborTowards(arg_0 -> ((Neighbors)neighbors).getLeftNeighbor(arg_0), SideChainPart.LEFT, list::addFirst);
        this.forEachNeighborTowards(arg_0 -> ((Neighbors)neighbors).getRightNeighbor(arg_0), SideChainPart.RIGHT, list::addLast);
        return list;
    }

    private void forEachNeighborTowards(IntFunction<Neighbor> neighborGetter, SideChainPart part, Consumer<BlockPos> posConsumer) {
        for (int i = 1; i < this.getMaxSideChainLength(); ++i) {
            Neighbor neighbor = neighborGetter.apply(i);
            if (neighbor.isCenterOr(part)) {
                posConsumer.accept(neighbor.pos());
            }
            if (neighbor.isNotCenter()) break;
        }
    }

    default public void disconnectNeighbors(WorldAccess world, BlockPos pos, BlockState state) {
        Neighbors neighbors = this.getNeighbors(world, pos, this.getFacing(state));
        neighbors.getLeftNeighbor().disconnectFromRight();
        neighbors.getRightNeighbor().disconnectFromLeft();
    }

    default public void connectNeighbors(WorldAccess world, BlockPos pos, BlockState state, BlockState oldState) {
        if (!this.canChainWith(state)) {
            return;
        }
        if (this.isAlreadyConnected(state, oldState)) {
            return;
        }
        Neighbors neighbors = this.getNeighbors(world, pos, this.getFacing(state));
        SideChainPart sideChainPart = SideChainPart.UNCONNECTED;
        int i = neighbors.getLeftNeighbor().isChained() ? this.getPositionsInChain(world, neighbors.getLeftNeighbor().pos()).size() : 0;
        int j = neighbors.getRightNeighbor().isChained() ? this.getPositionsInChain(world, neighbors.getRightNeighbor().pos()).size() : 0;
        int k = 1;
        if (this.canAddChainLength(i, k)) {
            sideChainPart = sideChainPart.connectToLeft();
            neighbors.getLeftNeighbor().connectToRight();
            k += i;
        }
        if (this.canAddChainLength(j, k)) {
            sideChainPart = sideChainPart.connectToRight();
            neighbors.getRightNeighbor().connectToLeft();
        }
        this.setSideChainPart(world, pos, sideChainPart);
    }

    private boolean canAddChainLength(int chainLength, int toAdd) {
        return chainLength > 0 && toAdd + chainLength <= this.getMaxSideChainLength();
    }

    private boolean isAlreadyConnected(BlockState state, BlockState oldState) {
        boolean bl = this.getSideChainPart(state).isConnected();
        boolean bl2 = this.canChainWith(oldState) && this.getSideChainPart(oldState).isConnected();
        return bl || bl2;
    }

    private Neighbors getNeighbors(WorldAccess world, BlockPos pos, Direction facing) {
        return new Neighbors(this, world, facing, pos, new HashMap());
    }

    default public void setSideChainPart(WorldAccess world, BlockPos pos, SideChainPart part) {
        BlockState blockState = world.getBlockState(pos);
        if (this.getSideChainPart(blockState) != part) {
            world.setBlockState(pos, this.withSideChainPart(blockState, part), 3);
        }
    }
}

