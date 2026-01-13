/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractCandleBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CandleBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Util
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

/*
 * Exception performing whole class analysis ignored.
 */
public class CandleBlock
extends AbstractCandleBlock
implements Waterloggable {
    public static final MapCodec<CandleBlock> CODEC = CandleBlock.createCodec(CandleBlock::new);
    public static final int field_31050 = 1;
    public static final int MAX_CANDLE_AMOUNT = 4;
    public static final IntProperty CANDLES = Properties.CANDLES;
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE = state -> (Boolean)state.get((Property)LIT) != false ? 3 * (Integer)state.get((Property)CANDLES) : 0;
    private static final Int2ObjectMap<List<Vec3d>> CANDLES_TO_PARTICLE_OFFSETS = (Int2ObjectMap)Util.make((Object)new Int2ObjectOpenHashMap(4), int2ObjectOpenHashMap -> {
        float f = 0.0625f;
        int2ObjectOpenHashMap.put(1, List.of(new Vec3d(8.0, 8.0, 8.0).multiply(0.0625)));
        int2ObjectOpenHashMap.put(2, List.of(new Vec3d(6.0, 7.0, 8.0).multiply(0.0625), new Vec3d(10.0, 8.0, 7.0).multiply(0.0625)));
        int2ObjectOpenHashMap.put(3, List.of(new Vec3d(8.0, 5.0, 10.0).multiply(0.0625), new Vec3d(6.0, 7.0, 8.0).multiply(0.0625), new Vec3d(9.0, 8.0, 7.0).multiply(0.0625)));
        int2ObjectOpenHashMap.put(4, List.of(new Vec3d(7.0, 5.0, 9.0).multiply(0.0625), new Vec3d(10.0, 7.0, 9.0).multiply(0.0625), new Vec3d(6.0, 7.0, 6.0).multiply(0.0625), new Vec3d(9.0, 8.0, 6.0).multiply(0.0625)));
    });
    private static final VoxelShape[] SHAPES_BY_CANDLES = new VoxelShape[]{Block.createColumnShape((double)2.0, (double)0.0, (double)6.0), Block.createCuboidShape((double)5.0, (double)0.0, (double)6.0, (double)11.0, (double)6.0, (double)9.0), Block.createCuboidShape((double)5.0, (double)0.0, (double)6.0, (double)10.0, (double)6.0, (double)11.0), Block.createCuboidShape((double)5.0, (double)0.0, (double)5.0, (double)11.0, (double)6.0, (double)10.0)};

    public MapCodec<CandleBlock> getCodec() {
        return CODEC;
    }

    public CandleBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)CANDLES, (Comparable)Integer.valueOf(1))).with((Property)LIT, (Comparable)Boolean.valueOf(false))).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isEmpty() && player.getAbilities().allowModifyWorld && ((Boolean)state.get((Property)LIT)).booleanValue()) {
            CandleBlock.extinguish((PlayerEntity)player, (BlockState)state, (WorldAccess)world, (BlockPos)pos);
            return ActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (!context.shouldCancelInteraction() && context.getStack().getItem() == this.asItem() && (Integer)state.get((Property)CANDLES) < 4) {
            return true;
        }
        return super.canReplace(state, context);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf((Block)this)) {
            return (BlockState)blockState.cycle((Property)CANDLES);
        }
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return (BlockState)super.getPlacementState(ctx).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(bl));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_CANDLES[(Integer)state.get((Property)CANDLES) - 1];
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{CANDLES, LIT, WATERLOGGED});
    }

    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue() || fluidState.getFluid() != Fluids.WATER) {
            return false;
        }
        BlockState blockState = (BlockState)state.with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(true));
        if (((Boolean)state.get((Property)LIT)).booleanValue()) {
            CandleBlock.extinguish(null, (BlockState)blockState, (WorldAccess)world, (BlockPos)pos);
        } else {
            world.setBlockState(pos, blockState, 3);
        }
        world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate((WorldView)world));
        return true;
    }

    public static boolean canBeLit(BlockState state) {
        return state.isIn(BlockTags.CANDLES, statex -> statex.contains((Property)LIT) && statex.contains((Property)WATERLOGGED)) && (Boolean)state.get((Property)LIT) == false && (Boolean)state.get((Property)WATERLOGGED) == false;
    }

    protected Iterable<Vec3d> getParticleOffsets(BlockState state) {
        return (Iterable)CANDLES_TO_PARTICLE_OFFSETS.get(((Integer)state.get((Property)CANDLES)).intValue());
    }

    protected boolean isNotLit(BlockState state) {
        return (Boolean)state.get((Property)WATERLOGGED) == false && super.isNotLit(state);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return Block.sideCoversSmallSquare((WorldView)world, (BlockPos)pos.down(), (Direction)Direction.UP);
    }
}

