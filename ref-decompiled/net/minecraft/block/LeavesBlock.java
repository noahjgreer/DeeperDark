/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.LeavesBlock
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.particle.ParticleUtil
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.OptionalInt;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public abstract class LeavesBlock
extends Block
implements Waterloggable {
    public static final int MAX_DISTANCE = 7;
    public static final IntProperty DISTANCE = Properties.DISTANCE_1_7;
    public static final BooleanProperty PERSISTENT = Properties.PERSISTENT;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    protected final float leafParticleChance;
    private static final int field_31112 = 1;
    private static boolean cutoutLeaves = true;

    public abstract MapCodec<? extends LeavesBlock> getCodec();

    public LeavesBlock(float leafParticleChance, AbstractBlock.Settings settings) {
        super(settings);
        this.leafParticleChance = leafParticleChance;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)DISTANCE, (Comparable)Integer.valueOf(7))).with((Property)PERSISTENT, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (!cutoutLeaves && stateFrom.getBlock() instanceof LeavesBlock) {
            return true;
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }

    public static void setCutoutLeaves(boolean cutoutLeaves) {
        LeavesBlock.cutoutLeaves = cutoutLeaves;
    }

    protected VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    protected boolean hasRandomTicks(BlockState state) {
        return (Integer)state.get((Property)DISTANCE) == 7 && (Boolean)state.get((Property)PERSISTENT) == false;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.shouldDecay(state)) {
            LeavesBlock.dropStacks((BlockState)state, (World)world, (BlockPos)pos);
            world.removeBlock(pos, false);
        }
    }

    protected boolean shouldDecay(BlockState state) {
        return (Boolean)state.get((Property)PERSISTENT) == false && (Integer)state.get((Property)DISTANCE) == 7;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, LeavesBlock.updateDistanceFromLogs((BlockState)state, (WorldAccess)world, (BlockPos)pos), 3);
    }

    protected int getOpacity(BlockState state) {
        return 1;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        int i;
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if ((i = LeavesBlock.getDistanceFromLog((BlockState)neighborState) + 1) != 1 || (Integer)state.get((Property)DISTANCE) != i) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        return state;
    }

    private static BlockState updateDistanceFromLogs(BlockState state, WorldAccess world, BlockPos pos) {
        int i = 7;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            mutable.set((Vec3i)pos, direction);
            i = Math.min(i, LeavesBlock.getDistanceFromLog((BlockState)world.getBlockState((BlockPos)mutable)) + 1);
            if (i == 1) break;
        }
        return (BlockState)state.with((Property)DISTANCE, (Comparable)Integer.valueOf(i));
    }

    private static int getDistanceFromLog(BlockState state) {
        return LeavesBlock.getOptionalDistanceFromLog((BlockState)state).orElse(7);
    }

    public static OptionalInt getOptionalDistanceFromLog(BlockState state) {
        if (state.isIn(BlockTags.LOGS)) {
            return OptionalInt.of(0);
        }
        if (state.contains((Property)DISTANCE)) {
            return OptionalInt.of((Integer)state.get((Property)DISTANCE));
        }
        return OptionalInt.empty();
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        LeavesBlock.spawnWaterParticle((World)world, (BlockPos)pos, (Random)random, (BlockState)blockState, (BlockPos)blockPos);
        this.spawnLeafParticle(world, pos, random, blockState, blockPos);
    }

    private static void spawnWaterParticle(World world, BlockPos pos, Random random, BlockState state, BlockPos posBelow) {
        if (!world.hasRain(pos.up())) {
            return;
        }
        if (random.nextInt(15) != 1) {
            return;
        }
        if (state.isOpaque() && state.isSideSolidFullSquare((BlockView)world, posBelow, Direction.UP)) {
            return;
        }
        ParticleUtil.spawnParticle((World)world, (BlockPos)pos, (Random)random, (ParticleEffect)ParticleTypes.DRIPPING_WATER);
    }

    private void spawnLeafParticle(World world, BlockPos pos, Random random, BlockState state, BlockPos posBelow) {
        if (random.nextFloat() >= this.leafParticleChance) {
            return;
        }
        if (LeavesBlock.isFaceFullSquare((VoxelShape)state.getCollisionShape((BlockView)world, posBelow), (Direction)Direction.UP)) {
            return;
        }
        this.spawnLeafParticle(world, pos, random);
    }

    protected abstract void spawnLeafParticle(World var1, BlockPos var2, Random var3);

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{DISTANCE, PERSISTENT, WATERLOGGED});
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with((Property)PERSISTENT, (Comparable)Boolean.valueOf(true))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
        return LeavesBlock.updateDistanceFromLogs((BlockState)blockState, (WorldAccess)ctx.getWorld(), (BlockPos)ctx.getBlockPos());
    }
}

