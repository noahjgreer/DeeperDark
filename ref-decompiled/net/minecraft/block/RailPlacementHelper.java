/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.block.AbstractRailBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.RailPlacementHelper
 *  net.minecraft.block.RailPlacementHelper$1
 *  net.minecraft.block.enums.RailShape
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Type
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public class RailPlacementHelper {
    private final World world;
    private final BlockPos pos;
    private final AbstractRailBlock block;
    private BlockState state;
    private final boolean forbidCurves;
    private final List<BlockPos> neighbors = Lists.newArrayList();

    public RailPlacementHelper(World world, BlockPos pos, BlockState state) {
        this.world = world;
        this.pos = pos;
        this.state = state;
        this.block = (AbstractRailBlock)state.getBlock();
        RailShape railShape = (RailShape)state.get(this.block.getShapeProperty());
        this.forbidCurves = this.block.cannotMakeCurves();
        this.computeNeighbors(railShape);
    }

    public List<BlockPos> getNeighbors() {
        return this.neighbors;
    }

    private void computeNeighbors(RailShape shape) {
        this.neighbors.clear();
        switch (1.field_11412[shape.ordinal()]) {
            case 1: {
                this.neighbors.add(this.pos.north());
                this.neighbors.add(this.pos.south());
                break;
            }
            case 2: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.east());
                break;
            }
            case 3: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.east().up());
                break;
            }
            case 4: {
                this.neighbors.add(this.pos.west().up());
                this.neighbors.add(this.pos.east());
                break;
            }
            case 5: {
                this.neighbors.add(this.pos.north().up());
                this.neighbors.add(this.pos.south());
                break;
            }
            case 6: {
                this.neighbors.add(this.pos.north());
                this.neighbors.add(this.pos.south().up());
                break;
            }
            case 7: {
                this.neighbors.add(this.pos.east());
                this.neighbors.add(this.pos.south());
                break;
            }
            case 8: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.south());
                break;
            }
            case 9: {
                this.neighbors.add(this.pos.west());
                this.neighbors.add(this.pos.north());
                break;
            }
            case 10: {
                this.neighbors.add(this.pos.east());
                this.neighbors.add(this.pos.north());
            }
        }
    }

    private void updateNeighborPositions() {
        for (int i = 0; i < this.neighbors.size(); ++i) {
            RailPlacementHelper railPlacementHelper = this.getNeighboringRail((BlockPos)this.neighbors.get(i));
            if (railPlacementHelper == null || !railPlacementHelper.isNeighbor(this)) {
                this.neighbors.remove(i--);
                continue;
            }
            this.neighbors.set(i, railPlacementHelper.pos);
        }
    }

    private boolean isVerticallyNearRail(BlockPos pos) {
        return AbstractRailBlock.isRail((World)this.world, (BlockPos)pos) || AbstractRailBlock.isRail((World)this.world, (BlockPos)pos.up()) || AbstractRailBlock.isRail((World)this.world, (BlockPos)pos.down());
    }

    private @Nullable RailPlacementHelper getNeighboringRail(BlockPos pos) {
        BlockPos blockPos = pos;
        BlockState blockState = this.world.getBlockState(blockPos);
        if (AbstractRailBlock.isRail((BlockState)blockState)) {
            return new RailPlacementHelper(this.world, blockPos, blockState);
        }
        blockPos = pos.up();
        blockState = this.world.getBlockState(blockPos);
        if (AbstractRailBlock.isRail((BlockState)blockState)) {
            return new RailPlacementHelper(this.world, blockPos, blockState);
        }
        blockPos = pos.down();
        blockState = this.world.getBlockState(blockPos);
        if (AbstractRailBlock.isRail((BlockState)blockState)) {
            return new RailPlacementHelper(this.world, blockPos, blockState);
        }
        return null;
    }

    private boolean isNeighbor(RailPlacementHelper other) {
        return this.isNeighbor(other.pos);
    }

    private boolean isNeighbor(BlockPos pos) {
        for (int i = 0; i < this.neighbors.size(); ++i) {
            BlockPos blockPos = (BlockPos)this.neighbors.get(i);
            if (blockPos.getX() != pos.getX() || blockPos.getZ() != pos.getZ()) continue;
            return true;
        }
        return false;
    }

    protected int getNeighborCount() {
        int i = 0;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!this.isVerticallyNearRail(this.pos.offset(direction))) continue;
            ++i;
        }
        return i;
    }

    private boolean canConnect(RailPlacementHelper placementHelper) {
        return this.isNeighbor(placementHelper) || this.neighbors.size() != 2;
    }

    private void computeRailShape(RailPlacementHelper placementHelper) {
        this.neighbors.add(placementHelper.pos);
        BlockPos blockPos = this.pos.north();
        BlockPos blockPos2 = this.pos.south();
        BlockPos blockPos3 = this.pos.west();
        BlockPos blockPos4 = this.pos.east();
        boolean bl = this.isNeighbor(blockPos);
        boolean bl2 = this.isNeighbor(blockPos2);
        boolean bl3 = this.isNeighbor(blockPos3);
        boolean bl4 = this.isNeighbor(blockPos4);
        RailShape railShape = null;
        if (bl || bl2) {
            railShape = RailShape.NORTH_SOUTH;
        }
        if (bl3 || bl4) {
            railShape = RailShape.EAST_WEST;
        }
        if (!this.forbidCurves) {
            if (bl2 && bl4 && !bl && !bl3) {
                railShape = RailShape.SOUTH_EAST;
            }
            if (bl2 && bl3 && !bl && !bl4) {
                railShape = RailShape.SOUTH_WEST;
            }
            if (bl && bl3 && !bl2 && !bl4) {
                railShape = RailShape.NORTH_WEST;
            }
            if (bl && bl4 && !bl2 && !bl3) {
                railShape = RailShape.NORTH_EAST;
            }
        }
        if (railShape == RailShape.NORTH_SOUTH) {
            if (AbstractRailBlock.isRail((World)this.world, (BlockPos)blockPos.up())) {
                railShape = RailShape.ASCENDING_NORTH;
            }
            if (AbstractRailBlock.isRail((World)this.world, (BlockPos)blockPos2.up())) {
                railShape = RailShape.ASCENDING_SOUTH;
            }
        }
        if (railShape == RailShape.EAST_WEST) {
            if (AbstractRailBlock.isRail((World)this.world, (BlockPos)blockPos4.up())) {
                railShape = RailShape.ASCENDING_EAST;
            }
            if (AbstractRailBlock.isRail((World)this.world, (BlockPos)blockPos3.up())) {
                railShape = RailShape.ASCENDING_WEST;
            }
        }
        if (railShape == null) {
            railShape = RailShape.NORTH_SOUTH;
        }
        this.state = (BlockState)this.state.with(this.block.getShapeProperty(), (Comparable)railShape);
        this.world.setBlockState(this.pos, this.state, 3);
    }

    private boolean canConnect(BlockPos pos) {
        RailPlacementHelper railPlacementHelper = this.getNeighboringRail(pos);
        if (railPlacementHelper == null) {
            return false;
        }
        railPlacementHelper.updateNeighborPositions();
        return railPlacementHelper.canConnect(this);
    }

    public RailPlacementHelper updateBlockState(boolean powered, boolean forceUpdate, RailShape railShape) {
        boolean bl10;
        boolean bl6;
        BlockPos blockPos = this.pos.north();
        BlockPos blockPos2 = this.pos.south();
        BlockPos blockPos3 = this.pos.west();
        BlockPos blockPos4 = this.pos.east();
        boolean bl = this.canConnect(blockPos);
        boolean bl2 = this.canConnect(blockPos2);
        boolean bl3 = this.canConnect(blockPos3);
        boolean bl4 = this.canConnect(blockPos4);
        RailShape railShape2 = null;
        boolean bl5 = bl || bl2;
        boolean bl7 = bl6 = bl3 || bl4;
        if (bl5 && !bl6) {
            railShape2 = RailShape.NORTH_SOUTH;
        }
        if (bl6 && !bl5) {
            railShape2 = RailShape.EAST_WEST;
        }
        boolean bl72 = bl2 && bl4;
        boolean bl8 = bl2 && bl3;
        boolean bl9 = bl && bl4;
        boolean bl11 = bl10 = bl && bl3;
        if (!this.forbidCurves) {
            if (bl72 && !bl && !bl3) {
                railShape2 = RailShape.SOUTH_EAST;
            }
            if (bl8 && !bl && !bl4) {
                railShape2 = RailShape.SOUTH_WEST;
            }
            if (bl10 && !bl2 && !bl4) {
                railShape2 = RailShape.NORTH_WEST;
            }
            if (bl9 && !bl2 && !bl3) {
                railShape2 = RailShape.NORTH_EAST;
            }
        }
        if (railShape2 == null) {
            if (bl5 && bl6) {
                railShape2 = railShape;
            } else if (bl5) {
                railShape2 = RailShape.NORTH_SOUTH;
            } else if (bl6) {
                railShape2 = RailShape.EAST_WEST;
            }
            if (!this.forbidCurves) {
                if (powered) {
                    if (bl72) {
                        railShape2 = RailShape.SOUTH_EAST;
                    }
                    if (bl8) {
                        railShape2 = RailShape.SOUTH_WEST;
                    }
                    if (bl9) {
                        railShape2 = RailShape.NORTH_EAST;
                    }
                    if (bl10) {
                        railShape2 = RailShape.NORTH_WEST;
                    }
                } else {
                    if (bl10) {
                        railShape2 = RailShape.NORTH_WEST;
                    }
                    if (bl9) {
                        railShape2 = RailShape.NORTH_EAST;
                    }
                    if (bl8) {
                        railShape2 = RailShape.SOUTH_WEST;
                    }
                    if (bl72) {
                        railShape2 = RailShape.SOUTH_EAST;
                    }
                }
            }
        }
        if (railShape2 == RailShape.NORTH_SOUTH) {
            if (AbstractRailBlock.isRail((World)this.world, (BlockPos)blockPos.up())) {
                railShape2 = RailShape.ASCENDING_NORTH;
            }
            if (AbstractRailBlock.isRail((World)this.world, (BlockPos)blockPos2.up())) {
                railShape2 = RailShape.ASCENDING_SOUTH;
            }
        }
        if (railShape2 == RailShape.EAST_WEST) {
            if (AbstractRailBlock.isRail((World)this.world, (BlockPos)blockPos4.up())) {
                railShape2 = RailShape.ASCENDING_EAST;
            }
            if (AbstractRailBlock.isRail((World)this.world, (BlockPos)blockPos3.up())) {
                railShape2 = RailShape.ASCENDING_WEST;
            }
        }
        if (railShape2 == null) {
            railShape2 = railShape;
        }
        this.computeNeighbors(railShape2);
        this.state = (BlockState)this.state.with(this.block.getShapeProperty(), (Comparable)railShape2);
        if (forceUpdate || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);
            for (int i = 0; i < this.neighbors.size(); ++i) {
                RailPlacementHelper railPlacementHelper = this.getNeighboringRail((BlockPos)this.neighbors.get(i));
                if (railPlacementHelper == null) continue;
                railPlacementHelper.updateNeighborPositions();
                if (!railPlacementHelper.canConnect(this)) continue;
                railPlacementHelper.computeRailShape(this);
            }
        }
        return this;
    }

    public BlockState getBlockState() {
        return this.state;
    }
}

