/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.FarmlandBlock
 *  net.minecraft.block.FenceGateBlock
 *  net.minecraft.block.PistonExtensionBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.registry.tag.FluidTags
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
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 *  net.minecraft.world.rule.GameRules
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class FarmlandBlock
extends Block {
    public static final MapCodec<FarmlandBlock> CODEC = FarmlandBlock.createCodec(FarmlandBlock::new);
    public static final IntProperty MOISTURE = Properties.MOISTURE;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)15.0);
    public static final int MAX_MOISTURE = 7;

    public MapCodec<FarmlandBlock> getCodec() {
        return CODEC;
    }

    public FarmlandBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)MOISTURE, (Comparable)Integer.valueOf(0)));
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (direction == Direction.UP && !state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, (Block)this, 1);
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.up());
        return !blockState.isSolid() || blockState.getBlock() instanceof FenceGateBlock || blockState.getBlock() instanceof PistonExtensionBlock;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if (!this.getDefaultState().canPlaceAt((WorldView)ctx.getWorld(), ctx.getBlockPos())) {
            return Blocks.DIRT.getDefaultState();
        }
        return super.getPlacementState(ctx);
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt((WorldView)world, pos)) {
            FarmlandBlock.setToDirt(null, (BlockState)state, (World)world, (BlockPos)pos);
        }
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int i = (Integer)state.get((Property)MOISTURE);
        if (FarmlandBlock.isWaterNearby((WorldView)world, (BlockPos)pos) || world.hasRain(pos.up())) {
            if (i < 7) {
                world.setBlockState(pos, (BlockState)state.with((Property)MOISTURE, (Comparable)Integer.valueOf(7)), 2);
            }
        } else if (i > 0) {
            world.setBlockState(pos, (BlockState)state.with((Property)MOISTURE, (Comparable)Integer.valueOf(i - 1)), 2);
        } else if (!FarmlandBlock.hasCrop((BlockView)world, (BlockPos)pos)) {
            FarmlandBlock.setToDirt(null, (BlockState)state, (World)world, (BlockPos)pos);
        }
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            if ((double)world.random.nextFloat() < fallDistance - 0.5 && entity instanceof LivingEntity && (entity instanceof PlayerEntity || ((Boolean)serverWorld.getGameRules().getValue(GameRules.DO_MOB_GRIEFING)).booleanValue()) && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512f) {
                FarmlandBlock.setToDirt((Entity)entity, (BlockState)state, (World)world, (BlockPos)pos);
            }
        }
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    public static void setToDirt(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        BlockState blockState = FarmlandBlock.pushEntitiesUpBeforeBlockChange((BlockState)state, (BlockState)Blocks.DIRT.getDefaultState(), (WorldAccess)world, (BlockPos)pos);
        world.setBlockState(pos, blockState);
        world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((Entity)entity, (BlockState)blockState));
    }

    private static boolean hasCrop(BlockView world, BlockPos pos) {
        return world.getBlockState(pos.up()).isIn(BlockTags.MAINTAINS_FARMLAND);
    }

    private static boolean isWaterNearby(WorldView world, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.iterate((BlockPos)pos.add(-4, 0, -4), (BlockPos)pos.add(4, 1, 4))) {
            if (!world.getFluidState(blockPos).isIn(FluidTags.WATER)) continue;
            return true;
        }
        return false;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{MOISTURE});
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

