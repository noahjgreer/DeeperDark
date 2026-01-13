/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BellBlock
 *  net.minecraft.block.BellBlock$1
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.WallMountedBlock
 *  net.minecraft.block.entity.BellBlockEntity
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.enums.Attachment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.tick.ScheduledTickView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BellBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Attachment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.ScheduledTickView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class BellBlock
extends BlockWithEntity {
    public static final MapCodec<BellBlock> CODEC = BellBlock.createCodec(BellBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<Attachment> ATTACHMENT = Properties.ATTACHMENT;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final VoxelShape BELL_SHAPE = VoxelShapes.union((VoxelShape)Block.createColumnShape((double)6.0, (double)6.0, (double)13.0), (VoxelShape)Block.createColumnShape((double)8.0, (double)4.0, (double)6.0));
    private static final VoxelShape CEILING_SHAPE = VoxelShapes.union((VoxelShape)BELL_SHAPE, (VoxelShape)Block.createColumnShape((double)2.0, (double)13.0, (double)16.0));
    private static final Map<Direction.Axis, VoxelShape> FLOOR_SHAPES = VoxelShapes.createHorizontalAxisShapeMap((VoxelShape)Block.createCuboidShape((double)16.0, (double)16.0, (double)8.0));
    private static final Map<Direction.Axis, VoxelShape> DOUBLE_WALL_SHAPES = VoxelShapes.createHorizontalAxisShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)BELL_SHAPE, (VoxelShape)Block.createColumnShape((double)2.0, (double)16.0, (double)13.0, (double)15.0)));
    private static final Map<Direction, VoxelShape> SINGLE_WALL_SHAPES = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)BELL_SHAPE, (VoxelShape)Block.createCuboidZShape((double)2.0, (double)13.0, (double)15.0, (double)0.0, (double)13.0)));
    public static final int field_31014 = 1;

    public MapCodec<BellBlock> getCodec() {
        return CODEC;
    }

    public BellBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)ATTACHMENT, (Comparable)Attachment.FLOOR)).with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != (Boolean)state.get((Property)POWERED)) {
            if (bl) {
                this.ring(world, pos, null);
            }
            world.setBlockState(pos, (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(bl)), 3);
        }
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        PlayerEntity playerEntity;
        Entity entity = projectile.getOwner();
        PlayerEntity playerEntity2 = entity instanceof PlayerEntity ? (playerEntity = (PlayerEntity)entity) : null;
        this.ring(world, state, hit, playerEntity2, true);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return this.ring(world, state, hit, player, true) ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    public boolean ring(World world, BlockState state, BlockHitResult hitResult, @Nullable PlayerEntity player, boolean checkHitPos) {
        boolean bl;
        Direction direction = hitResult.getSide();
        BlockPos blockPos = hitResult.getBlockPos();
        boolean bl2 = bl = !checkHitPos || this.isPointOnBell(state, direction, hitResult.getPos().y - (double)blockPos.getY());
        if (bl) {
            boolean bl22 = this.ring((Entity)player, world, blockPos, direction);
            if (bl22 && player != null) {
                player.incrementStat(Stats.BELL_RING);
            }
            return true;
        }
        return false;
    }

    private boolean isPointOnBell(BlockState state, Direction side, double y) {
        if (side.getAxis() == Direction.Axis.Y || y > (double)0.8124f) {
            return false;
        }
        Direction direction = (Direction)state.get((Property)FACING);
        Attachment attachment = (Attachment)state.get((Property)ATTACHMENT);
        switch (1.field_16327[attachment.ordinal()]) {
            case 1: {
                return direction.getAxis() == side.getAxis();
            }
            case 2: 
            case 3: {
                return direction.getAxis() != side.getAxis();
            }
            case 4: {
                return true;
            }
        }
        return false;
    }

    public boolean ring(World world, BlockPos pos, @Nullable Direction direction) {
        return this.ring(null, world, pos, direction);
    }

    public boolean ring(@Nullable Entity entity, World world, BlockPos pos, @Nullable Direction direction) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!world.isClient() && blockEntity instanceof BellBlockEntity) {
            if (direction == null) {
                direction = (Direction)world.getBlockState(pos).get((Property)FACING);
            }
            ((BellBlockEntity)blockEntity).activate(direction);
            world.playSound(null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0f, 1.0f);
            world.emitGameEvent(entity, (RegistryEntry)GameEvent.BLOCK_CHANGE, pos);
            return true;
        }
        return false;
    }

    private VoxelShape getShape(BlockState state) {
        Direction direction = (Direction)state.get((Property)FACING);
        return switch (1.field_16327[((Attachment)state.get((Property)ATTACHMENT)).ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> (VoxelShape)FLOOR_SHAPES.get(direction.getAxis());
            case 4 -> CEILING_SHAPE;
            case 2 -> (VoxelShape)SINGLE_WALL_SHAPES.get(direction);
            case 3 -> (VoxelShape)DOUBLE_WALL_SHAPES.get(direction.getAxis());
        };
    }

    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getShape(state);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getShape(state);
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        Direction.Axis axis = direction.getAxis();
        if (axis == Direction.Axis.Y) {
            BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with((Property)ATTACHMENT, (Comparable)(direction == Direction.DOWN ? Attachment.CEILING : Attachment.FLOOR))).with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing());
            if (blockState.canPlaceAt((WorldView)ctx.getWorld(), blockPos)) {
                return blockState;
            }
        } else {
            boolean bl = axis == Direction.Axis.X && world.getBlockState(blockPos.west()).isSideSolidFullSquare((BlockView)world, blockPos.west(), Direction.EAST) && world.getBlockState(blockPos.east()).isSideSolidFullSquare((BlockView)world, blockPos.east(), Direction.WEST) || axis == Direction.Axis.Z && world.getBlockState(blockPos.north()).isSideSolidFullSquare((BlockView)world, blockPos.north(), Direction.SOUTH) && world.getBlockState(blockPos.south()).isSideSolidFullSquare((BlockView)world, blockPos.south(), Direction.NORTH);
            BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)direction.getOpposite())).with((Property)ATTACHMENT, (Comparable)(bl ? Attachment.DOUBLE_WALL : Attachment.SINGLE_WALL));
            if (blockState.canPlaceAt((WorldView)ctx.getWorld(), ctx.getBlockPos())) {
                return blockState;
            }
            boolean bl2 = world.getBlockState(blockPos.down()).isSideSolidFullSquare((BlockView)world, blockPos.down(), Direction.UP);
            if ((blockState = (BlockState)blockState.with((Property)ATTACHMENT, (Comparable)(bl2 ? Attachment.FLOOR : Attachment.CEILING))).canPlaceAt((WorldView)ctx.getWorld(), ctx.getBlockPos())) {
                return blockState;
            }
        }
        return null;
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.canTriggerBlocks()) {
            this.ring((World)world, pos, null);
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        Attachment attachment = (Attachment)state.get((Property)ATTACHMENT);
        Direction direction2 = BellBlock.getPlacementSide((BlockState)state).getOpposite();
        if (direction2 == direction && !state.canPlaceAt(world, pos) && attachment != Attachment.DOUBLE_WALL) {
            return Blocks.AIR.getDefaultState();
        }
        if (direction.getAxis() == ((Direction)state.get((Property)FACING)).getAxis()) {
            if (attachment == Attachment.DOUBLE_WALL && !neighborState.isSideSolidFullSquare((BlockView)world, neighborPos, direction)) {
                return (BlockState)((BlockState)state.with((Property)ATTACHMENT, (Comparable)Attachment.SINGLE_WALL)).with((Property)FACING, (Comparable)direction.getOpposite());
            }
            if (attachment == Attachment.SINGLE_WALL && direction2.getOpposite() == direction && neighborState.isSideSolidFullSquare((BlockView)world, neighborPos, (Direction)state.get((Property)FACING))) {
                return (BlockState)state.with((Property)ATTACHMENT, (Comparable)Attachment.DOUBLE_WALL);
            }
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = BellBlock.getPlacementSide((BlockState)state).getOpposite();
        if (direction == Direction.UP) {
            return Block.sideCoversSmallSquare((WorldView)world, (BlockPos)pos.up(), (Direction)Direction.DOWN);
        }
        return WallMountedBlock.canPlaceAt((WorldView)world, (BlockPos)pos, (Direction)direction);
    }

    private static Direction getPlacementSide(BlockState state) {
        switch (1.field_16327[((Attachment)state.get((Property)ATTACHMENT)).ordinal()]) {
            case 4: {
                return Direction.DOWN;
            }
            case 1: {
                return Direction.UP;
            }
        }
        return ((Direction)state.get((Property)FACING)).getOpposite();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, ATTACHMENT, POWERED});
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BellBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BellBlock.validateTicker(type, (BlockEntityType)BlockEntityType.BELL, (BlockEntityTicker)(world.isClient() ? BellBlockEntity::clientTick : BellBlockEntity::serverTick));
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }
}

