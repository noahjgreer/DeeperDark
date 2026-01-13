/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.BubbleColumnBlock
 *  net.minecraft.block.FluidDrainable
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
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
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BubbleColumnBlock
extends Block
implements FluidDrainable {
    public static final MapCodec<BubbleColumnBlock> CODEC = BubbleColumnBlock.createCodec(BubbleColumnBlock::new);
    public static final BooleanProperty DRAG = Properties.DRAG;
    private static final int SCHEDULED_TICK_DELAY = 5;

    public MapCodec<BubbleColumnBlock> getCodec() {
        return CODEC;
    }

    public BubbleColumnBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)DRAG, (Comparable)Boolean.valueOf(true)));
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (bl) {
            boolean bl2;
            BlockState blockState = world.getBlockState(pos.up());
            boolean bl3 = bl2 = blockState.getCollisionShape((BlockView)world, pos).isEmpty() && blockState.getFluidState().isEmpty();
            if (bl2) {
                entity.onBubbleColumnSurfaceCollision(((Boolean)state.get((Property)DRAG)).booleanValue(), pos);
            } else {
                entity.onBubbleColumnCollision(((Boolean)state.get((Property)DRAG)).booleanValue());
            }
        }
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BubbleColumnBlock.update((WorldAccess)world, (BlockPos)pos, (BlockState)state, (BlockState)world.getBlockState(pos.down()));
    }

    protected FluidState getFluidState(BlockState state) {
        return Fluids.WATER.getStill(false);
    }

    public static void update(WorldAccess world, BlockPos pos, BlockState state) {
        BubbleColumnBlock.update((WorldAccess)world, (BlockPos)pos, (BlockState)world.getBlockState(pos), (BlockState)state);
    }

    public static void update(WorldAccess world, BlockPos pos, BlockState water, BlockState bubbleSource) {
        if (!BubbleColumnBlock.isStillWater((BlockState)water)) {
            return;
        }
        BlockState blockState = BubbleColumnBlock.getBubbleState((BlockState)bubbleSource);
        world.setBlockState(pos, blockState, 2);
        BlockPos.Mutable mutable = pos.mutableCopy().move(Direction.UP);
        while (BubbleColumnBlock.isStillWater((BlockState)world.getBlockState((BlockPos)mutable))) {
            if (!world.setBlockState((BlockPos)mutable, blockState, 2)) {
                return;
            }
            mutable.move(Direction.UP);
        }
    }

    private static boolean isStillWater(BlockState state) {
        return state.isOf(Blocks.BUBBLE_COLUMN) || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() >= 8 && state.getFluidState().isStill();
    }

    private static BlockState getBubbleState(BlockState state) {
        if (state.isOf(Blocks.BUBBLE_COLUMN)) {
            return state;
        }
        if (state.isOf(Blocks.SOUL_SAND)) {
            return (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with((Property)DRAG, (Comparable)Boolean.valueOf(false));
        }
        if (state.isOf(Blocks.MAGMA_BLOCK)) {
            return (BlockState)Blocks.BUBBLE_COLUMN.getDefaultState().with((Property)DRAG, (Comparable)Boolean.valueOf(true));
        }
        return Blocks.WATER.getDefaultState();
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d = pos.getX();
        double e = pos.getY();
        double f = pos.getZ();
        if (((Boolean)state.get((Property)DRAG)).booleanValue()) {
            world.addImportantParticleClient((ParticleEffect)ParticleTypes.CURRENT_DOWN, d + 0.5, e + 0.8, f, 0.0, 0.0, 0.0);
            if (random.nextInt(200) == 0) {
                world.playSoundClient(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        } else {
            world.addImportantParticleClient((ParticleEffect)ParticleTypes.BUBBLE_COLUMN_UP, d + 0.5, e, f + 0.5, 0.0, 0.04, 0.0);
            world.addImportantParticleClient((ParticleEffect)ParticleTypes.BUBBLE_COLUMN_UP, d + (double)random.nextFloat(), e + (double)random.nextFloat(), f + (double)random.nextFloat(), 0.0, 0.04, 0.0);
            if (random.nextInt(200) == 0) {
                world.playSoundClient(d, e, f, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
        }
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        if (!state.canPlaceAt(world, pos) || direction == Direction.DOWN || direction == Direction.UP && !neighborState.isOf(Blocks.BUBBLE_COLUMN) && BubbleColumnBlock.isStillWater((BlockState)neighborState)) {
            tickView.scheduleBlockTick(pos, (Block)this, 5);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        return blockState.isOf(Blocks.BUBBLE_COLUMN) || blockState.isOf(Blocks.MAGMA_BLOCK) || blockState.isOf(Blocks.SOUL_SAND);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{DRAG});
    }

    public ItemStack tryDrainFluid(@Nullable LivingEntity drainer, WorldAccess world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
        return new ItemStack((ItemConvertible)Items.WATER_BUCKET);
    }

    public Optional<SoundEvent> getBucketFillSound() {
        return Fluids.WATER.getBucketFillSound();
    }
}

