/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.HangingMossBlock
 *  net.minecraft.block.MultifaceBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class HangingMossBlock
extends Block
implements Fertilizable {
    public static final MapCodec<HangingMossBlock> CODEC = HangingMossBlock.createCodec(HangingMossBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)16.0);
    private static final VoxelShape TIP_SHAPE = Block.createColumnShape((double)14.0, (double)2.0, (double)16.0);
    public static final BooleanProperty TIP = Properties.TIP;

    public MapCodec<HangingMossBlock> getCodec() {
        return CODEC;
    }

    public HangingMossBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)TIP, (Comparable)Boolean.valueOf(true)));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (Boolean)state.get((Property)TIP) != false ? TIP_SHAPE : SHAPE;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockState blockState;
        if (random.nextInt(500) == 0 && ((blockState = world.getBlockState(pos.up())).isIn(BlockTags.PALE_OAK_LOGS) || blockState.isOf(Blocks.PALE_OAK_LEAVES))) {
            world.playSoundClient((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_PALE_HANGING_MOSS_IDLE, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
        }
    }

    protected boolean isTransparent(BlockState state) {
        return true;
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return this.canPlaceAt((BlockView)world, pos);
    }

    private boolean canPlaceAt(BlockView world, BlockPos pos) {
        BlockState blockState;
        BlockPos blockPos = pos.offset(Direction.UP);
        return MultifaceBlock.canGrowOn((BlockView)world, (Direction)Direction.UP, (BlockPos)blockPos, (BlockState)(blockState = world.getBlockState(blockPos))) || blockState.isOf(Blocks.PALE_HANGING_MOSS);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (!this.canPlaceAt((BlockView)world, pos)) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        return (BlockState)state.with((Property)TIP, (Comparable)Boolean.valueOf(!world.getBlockState(pos.down()).isOf((Block)this)));
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.canPlaceAt((BlockView)world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{TIP});
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return this.canGrowInto(world.getBlockState(this.getTipPos((BlockView)world, pos).down()));
    }

    private boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    public BlockPos getTipPos(BlockView world, BlockPos pos) {
        BlockState blockState;
        BlockPos.Mutable mutable = pos.mutableCopy();
        do {
            mutable.move(Direction.DOWN);
        } while ((blockState = world.getBlockState((BlockPos)mutable)).isOf((Block)this));
        return mutable.offset(Direction.UP).toImmutable();
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos blockPos = this.getTipPos((BlockView)world, pos).down();
        if (!this.canGrowInto(world.getBlockState(blockPos))) {
            return;
        }
        world.setBlockState(blockPos, (BlockState)state.with((Property)TIP, (Comparable)Boolean.valueOf(true)));
    }
}

