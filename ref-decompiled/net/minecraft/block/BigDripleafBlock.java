/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Object2IntArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BigDripleafBlock
 *  net.minecraft.block.BigDripleafStemBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Fertilizable
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.enums.Tilt
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Util
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.HeightLimitView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BigDripleafStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.Tilt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BigDripleafBlock
extends HorizontalFacingBlock
implements Fertilizable,
Waterloggable {
    public static final MapCodec<BigDripleafBlock> CODEC = BigDripleafBlock.createCodec(BigDripleafBlock::new);
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final EnumProperty<Tilt> TILT = Properties.TILT;
    private static final int field_31015 = -1;
    private static final Object2IntMap<Tilt> NEXT_TILT_DELAYS = (Object2IntMap)Util.make((Object)new Object2IntArrayMap(), delays -> {
        delays.defaultReturnValue(-1);
        delays.put((Object)Tilt.UNSTABLE, 10);
        delays.put((Object)Tilt.PARTIAL, 10);
        delays.put((Object)Tilt.FULL, 100);
    });
    private static final int field_31016 = 5;
    private static final int field_31018 = 11;
    private static final int field_31019 = 13;
    private static final Map<Tilt, VoxelShape> SHAPES_BY_TILT = Maps.newEnumMap(Map.of(Tilt.NONE, Block.createColumnShape((double)16.0, (double)11.0, (double)15.0), Tilt.UNSTABLE, Block.createColumnShape((double)16.0, (double)11.0, (double)15.0), Tilt.PARTIAL, Block.createColumnShape((double)16.0, (double)11.0, (double)13.0), Tilt.FULL, VoxelShapes.empty()));
    private final Function<BlockState, VoxelShape> shapeFunction;

    public MapCodec<BigDripleafBlock> getCodec() {
        return CODEC;
    }

    public BigDripleafBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)TILT, (Comparable)Tilt.NONE));
        this.shapeFunction = this.createShapeFunction();
    }

    private Function<BlockState, VoxelShape> createShapeFunction() {
        Map map = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createColumnShape((double)6.0, (double)0.0, (double)13.0).offset(0.0, 0.0, 0.25).simplify());
        return this.createShapeFunction(state -> VoxelShapes.union((VoxelShape)((VoxelShape)SHAPES_BY_TILT.get(state.get((Property)TILT))), (VoxelShape)((VoxelShape)map.get(state.get((Property)FACING)))), new Property[]{WATERLOGGED});
    }

    public static void grow(WorldAccess world, Random random, BlockPos pos, Direction direction) {
        int j;
        int i = MathHelper.nextInt((Random)random, (int)2, (int)5);
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (j = 0; j < i && BigDripleafBlock.canGrowInto((HeightLimitView)world, (BlockPos)mutable, (BlockState)world.getBlockState((BlockPos)mutable)); ++j) {
            mutable.move(Direction.UP);
        }
        int k = pos.getY() + j - 1;
        mutable.setY(pos.getY());
        while (mutable.getY() < k) {
            BigDripleafStemBlock.placeStemAt((WorldAccess)world, (BlockPos)mutable, (FluidState)world.getFluidState((BlockPos)mutable), (Direction)direction);
            mutable.move(Direction.UP);
        }
        BigDripleafBlock.placeDripleafAt((WorldAccess)world, (BlockPos)mutable, (FluidState)world.getFluidState((BlockPos)mutable), (Direction)direction);
    }

    private static boolean canGrowInto(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.SMALL_DRIPLEAF);
    }

    protected static boolean canGrowInto(HeightLimitView world, BlockPos pos, BlockState state) {
        return !world.isOutOfHeightLimit(pos) && BigDripleafBlock.canGrowInto((BlockState)state);
    }

    protected static boolean placeDripleafAt(WorldAccess world, BlockPos pos, FluidState fluidState, Direction direction) {
        BlockState blockState = (BlockState)((BlockState)Blocks.BIG_DRIPLEAF.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.isEqualAndStill((Fluid)Fluids.WATER)))).with((Property)FACING, (Comparable)direction);
        return world.setBlockState(pos, blockState, 3);
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        this.changeTilt(state, world, hit.getBlockPos(), Tilt.FULL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isOf((Block)this) || blockState.isOf(Blocks.BIG_DRIPLEAF_STEM) || blockState.isIn(BlockTags.BIG_DRIPLEAF_PLACEABLE);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == Direction.UP && neighborState.isOf((Block)this)) {
            return Blocks.BIG_DRIPLEAF_STEM.getStateWithProperties(state);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        BlockState blockState = world.getBlockState(pos.up());
        return BigDripleafBlock.canGrowInto((BlockState)blockState);
    }

    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockState blockState;
        BlockPos blockPos = pos.up();
        if (BigDripleafBlock.canGrowInto((HeightLimitView)world, (BlockPos)blockPos, (BlockState)(blockState = world.getBlockState(blockPos)))) {
            Direction direction = (Direction)state.get((Property)FACING);
            BigDripleafStemBlock.placeStemAt((WorldAccess)world, (BlockPos)pos, (FluidState)state.getFluidState(), (Direction)direction);
            BigDripleafBlock.placeDripleafAt((WorldAccess)world, (BlockPos)blockPos, (FluidState)blockState.getFluidState(), (Direction)direction);
        }
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world.isClient()) {
            return;
        }
        if (state.get((Property)TILT) == Tilt.NONE && BigDripleafBlock.isEntityAbove((BlockPos)pos, (Entity)entity) && !world.isReceivingRedstonePower(pos)) {
            this.changeTilt(state, world, pos, Tilt.UNSTABLE, null);
        }
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isReceivingRedstonePower(pos)) {
            BigDripleafBlock.resetTilt((BlockState)state, (World)world, (BlockPos)pos);
            return;
        }
        Tilt tilt = (Tilt)state.get((Property)TILT);
        if (tilt == Tilt.UNSTABLE) {
            this.changeTilt(state, (World)world, pos, Tilt.PARTIAL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
        } else if (tilt == Tilt.PARTIAL) {
            this.changeTilt(state, (World)world, pos, Tilt.FULL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
        } else if (tilt == Tilt.FULL) {
            BigDripleafBlock.resetTilt((BlockState)state, (World)world, (BlockPos)pos);
        }
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) {
            BigDripleafBlock.resetTilt((BlockState)state, (World)world, (BlockPos)pos);
        }
    }

    private static void playTiltSound(World world, BlockPos pos, SoundEvent soundEvent) {
        float f = MathHelper.nextBetween((Random)world.random, (float)0.8f, (float)1.2f);
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, f);
    }

    private static boolean isEntityAbove(BlockPos pos, Entity entity) {
        return entity.isOnGround() && entity.getEntityPos().y > (double)((float)pos.getY() + 0.6875f);
    }

    private void changeTilt(BlockState state, World world, BlockPos pos, Tilt tilt, @Nullable SoundEvent sound) {
        int i;
        BigDripleafBlock.changeTilt((BlockState)state, (World)world, (BlockPos)pos, (Tilt)tilt);
        if (sound != null) {
            BigDripleafBlock.playTiltSound((World)world, (BlockPos)pos, (SoundEvent)sound);
        }
        if ((i = NEXT_TILT_DELAYS.getInt((Object)tilt)) != -1) {
            world.scheduleBlockTick(pos, (Block)this, i);
        }
    }

    private static void resetTilt(BlockState state, World world, BlockPos pos) {
        BigDripleafBlock.changeTilt((BlockState)state, (World)world, (BlockPos)pos, (Tilt)Tilt.NONE);
        if (state.get((Property)TILT) != Tilt.NONE) {
            BigDripleafBlock.playTiltSound((World)world, (BlockPos)pos, (SoundEvent)SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
        }
    }

    private static void changeTilt(BlockState state, World world, BlockPos pos, Tilt tilt) {
        Tilt tilt2 = (Tilt)state.get((Property)TILT);
        world.setBlockState(pos, (BlockState)state.with((Property)TILT, (Comparable)tilt), 2);
        if (tilt.isStable() && tilt != tilt2) {
            world.emitGameEvent(null, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
        }
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)SHAPES_BY_TILT.get(state.get((Property)TILT));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().down());
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = blockState.isOf(Blocks.BIG_DRIPLEAF) || blockState.isOf(Blocks.BIG_DRIPLEAF_STEM);
        return (BlockState)((BlockState)this.getDefaultState().with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.isEqualAndStill((Fluid)Fluids.WATER)))).with((Property)FACING, (Comparable)(bl ? (Direction)blockState.get((Property)FACING) : ctx.getHorizontalPlayerFacing().getOpposite()));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{WATERLOGGED, FACING, TILT});
    }
}

