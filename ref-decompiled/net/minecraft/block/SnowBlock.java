/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.SnowBlock
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.LightType
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class SnowBlock
extends Block {
    public static final MapCodec<SnowBlock> CODEC = SnowBlock.createCodec(SnowBlock::new);
    public static final int MAX_LAYERS = 8;
    public static final IntProperty LAYERS = Properties.LAYERS;
    private static final VoxelShape[] SHAPES_BY_LAYERS = Block.createShapeArray((int)8, layers -> Block.createColumnShape((double)16.0, (double)0.0, (double)(layers * 2)));
    public static final int field_31248 = 5;

    public MapCodec<SnowBlock> getCodec() {
        return CODEC;
    }

    public SnowBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)LAYERS, (Comparable)Integer.valueOf(1)));
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        if (type == NavigationType.LAND) {
            return (Integer)state.get((Property)LAYERS) < 5;
        }
        return false;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_LAYERS[(Integer)state.get((Property)LAYERS)];
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_LAYERS[(Integer)state.get((Property)LAYERS) - 1];
    }

    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPES_BY_LAYERS[(Integer)state.get((Property)LAYERS)];
    }

    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_LAYERS[(Integer)state.get((Property)LAYERS)];
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return (Integer)state.get((Property)LAYERS) == 8 ? 0.2f : 1.0f;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        if (blockState.isIn(BlockTags.SNOW_LAYER_CANNOT_SURVIVE_ON)) {
            return false;
        }
        if (blockState.isIn(BlockTags.SNOW_LAYER_CAN_SURVIVE_ON)) {
            return true;
        }
        return Block.isFaceFullSquare((VoxelShape)blockState.getCollisionShape((BlockView)world, pos.down()), (Direction)Direction.UP) || blockState.isOf((Block)this) && (Integer)blockState.get((Property)LAYERS) == 8;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getLightLevel(LightType.BLOCK, pos) > 11) {
            SnowBlock.dropStacks((BlockState)state, (World)world, (BlockPos)pos);
            world.removeBlock(pos, false);
        }
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        int i = (Integer)state.get((Property)LAYERS);
        if (context.getStack().isOf(this.asItem()) && i < 8) {
            if (context.canReplaceExisting()) {
                return context.getSide() == Direction.UP;
            }
            return true;
        }
        return i == 1;
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf((Block)this)) {
            int i = (Integer)blockState.get((Property)LAYERS);
            return (BlockState)blockState.with((Property)LAYERS, (Comparable)Integer.valueOf(Math.min(8, i + 1)));
        }
        return super.getPlacementState(ctx);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LAYERS});
    }
}

