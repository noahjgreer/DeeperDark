/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractChestBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.DoubleBlockProperties$PropertyRetriever
 *  net.minecraft.block.DoubleBlockProperties$PropertySource
 *  net.minecraft.block.EnderChestBlock
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.Waterloggable
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.ChestBlockEntity
 *  net.minecraft.block.entity.EnderChestBlockEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.mob.PiglinBrain
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.inventory.EnderChestInventory
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.screen.GenericContainerScreenHandler
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.SimpleNamedScreenHandlerFactory
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class EnderChestBlock
extends AbstractChestBlock<EnderChestBlockEntity>
implements Waterloggable {
    public static final MapCodec<EnderChestBlock> CODEC = EnderChestBlock.createCodec(EnderChestBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)14.0);
    private static final Text CONTAINER_NAME = Text.translatable((String)"container.enderchest");

    public MapCodec<EnderChestBlock> getCodec() {
        return CODEC;
    }

    public EnderChestBlock(AbstractBlock.Settings settings) {
        super(settings, () -> BlockEntityType.ENDER_CHEST);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
        return DoubleBlockProperties.PropertyRetriever::getFallback;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        return (BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite())).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(fluidState.getFluid() == Fluids.WATER));
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player2, BlockHitResult hit) {
        EnderChestInventory enderChestInventory = player2.getEnderChestInventory();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (enderChestInventory == null || !(blockEntity instanceof EnderChestBlockEntity)) {
            return ActionResult.SUCCESS;
        }
        EnderChestBlockEntity enderChestBlockEntity = (EnderChestBlockEntity)blockEntity;
        BlockPos blockPos = pos.up();
        if (world.getBlockState(blockPos).isSolidBlock((BlockView)world, blockPos)) {
            return ActionResult.SUCCESS;
        }
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            enderChestInventory.setActiveBlockEntity(enderChestBlockEntity);
            player2.openHandledScreen((NamedScreenHandlerFactory)new SimpleNamedScreenHandlerFactory((syncId, playerInventory, player) -> GenericContainerScreenHandler.createGeneric9x3((int)syncId, (PlayerInventory)playerInventory, (Inventory)enderChestInventory), CONTAINER_NAME));
            player2.incrementStat(Stats.OPEN_ENDERCHEST);
            PiglinBrain.onGuardedBlockInteracted((ServerWorld)serverWorld, (PlayerEntity)player2, (boolean)true);
        }
        return ActionResult.SUCCESS;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnderChestBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? EnderChestBlock.validateTicker(type, (BlockEntityType)BlockEntityType.ENDER_CHEST, EnderChestBlockEntity::clientTick) : null;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        for (int i = 0; i < 3; ++i) {
            int j = random.nextInt(2) * 2 - 1;
            int k = random.nextInt(2) * 2 - 1;
            double d = (double)pos.getX() + 0.5 + 0.25 * (double)j;
            double e = (float)pos.getY() + random.nextFloat();
            double f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
            double g = random.nextFloat() * (float)j;
            double h = ((double)random.nextFloat() - 0.5) * 0.125;
            double l = random.nextFloat() * (float)k;
            world.addParticleClient((ParticleEffect)ParticleTypes.PORTAL, d, e, f, g, h, l);
        }
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, WATERLOGGED});
    }

    protected FluidState getFluidState(BlockState state) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (((Boolean)state.get((Property)WATERLOGGED)).booleanValue()) {
            tickView.scheduleFluidTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnderChestBlockEntity) {
            ((EnderChestBlockEntity)blockEntity).onScheduledTick();
        }
    }
}

