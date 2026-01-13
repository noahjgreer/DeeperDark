/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.WallMountedBlock
 *  net.minecraft.block.WallMountedBlock$1
 *  net.minecraft.block.enums.BlockFace
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class WallMountedBlock
extends HorizontalFacingBlock {
    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;

    protected WallMountedBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected abstract MapCodec<? extends WallMountedBlock> getCodec();

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return WallMountedBlock.canPlaceAt((WorldView)world, (BlockPos)pos, (Direction)WallMountedBlock.getDirection((BlockState)state).getOpposite());
    }

    public static boolean canPlaceAt(WorldView world, BlockPos pos, Direction direction) {
        BlockPos blockPos = pos.offset(direction);
        return world.getBlockState(blockPos).isSideSolidFullSquare((BlockView)world, blockPos, direction.getOpposite());
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        for (Direction direction : ctx.getPlacementDirections()) {
            BlockState blockState = direction.getAxis() == Direction.Axis.Y ? (BlockState)((BlockState)this.getDefaultState().with((Property)FACE, (Comparable)(direction == Direction.UP ? BlockFace.CEILING : BlockFace.FLOOR))).with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing()) : (BlockState)((BlockState)this.getDefaultState().with((Property)FACE, (Comparable)BlockFace.WALL)).with((Property)FACING, (Comparable)direction.getOpposite());
            if (!blockState.canPlaceAt((WorldView)ctx.getWorld(), ctx.getBlockPos())) continue;
            return blockState;
        }
        return null;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (WallMountedBlock.getDirection((BlockState)state).getOpposite() == direction && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected static Direction getDirection(BlockState state) {
        switch (1.field_11008[((BlockFace)state.get((Property)FACE)).ordinal()]) {
            case 1: {
                return Direction.DOWN;
            }
            case 2: {
                return Direction.UP;
            }
        }
        return (Direction)state.get((Property)FACING);
    }
}

