/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.DoorBlock
 *  net.minecraft.block.DoorBlock$1
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.TallPlantBlock
 *  net.minecraft.block.enums.DoorHinge
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
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
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
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

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
public class DoorBlock
extends Block {
    public static final MapCodec<DoorBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter(DoorBlock::getBlockSetType), (App)DoorBlock.createSettingsCodec()).apply((Applicative)instance, DoorBlock::new));
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<DoorHinge> HINGE = Properties.DOOR_HINGE;
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final Map<Direction, VoxelShape> SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)16.0, (double)13.0, (double)16.0));
    private final BlockSetType blockSetType;

    public MapCodec<? extends DoorBlock> getCodec() {
        return CODEC;
    }

    public DoorBlock(BlockSetType type, AbstractBlock.Settings settings) {
        super(settings.sounds(type.soundType()));
        this.blockSetType = type;
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with((Property)OPEN, (Comparable)Boolean.valueOf(false))).with((Property)HINGE, (Comparable)DoorHinge.LEFT)).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)HALF, (Comparable)DoubleBlockHalf.LOWER));
    }

    public BlockSetType getBlockSetType() {
        return this.blockSetType;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = (Direction)state.get((Property)FACING);
        Direction direction2 = ((Boolean)state.get((Property)OPEN)).booleanValue() ? (state.get((Property)HINGE) == DoorHinge.RIGHT ? direction.rotateYCounterclockwise() : direction.rotateYClockwise()) : direction;
        return (VoxelShape)SHAPES_BY_DIRECTION.get(direction2);
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf)state.get((Property)HALF);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            if (neighborState.getBlock() instanceof DoorBlock && neighborState.get((Property)HALF) != doubleBlockHalf) {
                return (BlockState)neighborState.with((Property)HALF, (Comparable)doubleBlockHalf);
            }
            return Blocks.AIR.getDefaultState();
        }
        if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.canTriggerBlocks() && state.get((Property)HALF) == DoubleBlockHalf.LOWER && this.blockSetType.canOpenByWindCharge() && !((Boolean)state.get((Property)POWERED)).booleanValue()) {
            this.setOpen(null, (World)world, state, pos, !this.isOpen(state));
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!(world.isClient() || !player.shouldSkipBlockDrops() && player.canHarvest(state))) {
            TallPlantBlock.onBreakInCreative((World)world, (BlockPos)pos, (BlockState)state, (PlayerEntity)player);
        }
        return super.onBreak(world, pos, state, player);
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return switch (1.field_10947[type.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1, 2 -> (Boolean)state.get((Property)OPEN);
            case 3 -> false;
        };
    }

    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (blockPos.getY() < world.getTopYInclusive() && world.getBlockState(blockPos.up()).canReplace(ctx)) {
            boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
            return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing())).with((Property)HINGE, (Comparable)this.getHinge(ctx))).with((Property)POWERED, (Comparable)Boolean.valueOf(bl))).with((Property)OPEN, (Comparable)Boolean.valueOf(bl))).with((Property)HALF, (Comparable)DoubleBlockHalf.LOWER);
        }
        return null;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), (BlockState)state.with((Property)HALF, (Comparable)DoubleBlockHalf.UPPER), 3);
    }

    private DoorHinge getHinge(ItemPlacementContext ctx) {
        boolean bl2;
        World blockView = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        Direction direction = ctx.getHorizontalPlayerFacing();
        BlockPos blockPos2 = blockPos.up();
        Direction direction2 = direction.rotateYCounterclockwise();
        BlockPos blockPos3 = blockPos.offset(direction2);
        BlockState blockState = blockView.getBlockState(blockPos3);
        BlockPos blockPos4 = blockPos2.offset(direction2);
        BlockState blockState2 = blockView.getBlockState(blockPos4);
        Direction direction3 = direction.rotateYClockwise();
        BlockPos blockPos5 = blockPos.offset(direction3);
        BlockState blockState3 = blockView.getBlockState(blockPos5);
        BlockPos blockPos6 = blockPos2.offset(direction3);
        BlockState blockState4 = blockView.getBlockState(blockPos6);
        int i = (blockState.isFullCube((BlockView)blockView, blockPos3) ? -1 : 0) + (blockState2.isFullCube((BlockView)blockView, blockPos4) ? -1 : 0) + (blockState3.isFullCube((BlockView)blockView, blockPos5) ? 1 : 0) + (blockState4.isFullCube((BlockView)blockView, blockPos6) ? 1 : 0);
        boolean bl = blockState.getBlock() instanceof DoorBlock && blockState.get((Property)HALF) == DoubleBlockHalf.LOWER;
        boolean bl3 = bl2 = blockState3.getBlock() instanceof DoorBlock && blockState3.get((Property)HALF) == DoubleBlockHalf.LOWER;
        if (bl && !bl2 || i > 0) {
            return DoorHinge.RIGHT;
        }
        if (bl2 && !bl || i < 0) {
            return DoorHinge.LEFT;
        }
        int j = direction.getOffsetX();
        int k = direction.getOffsetZ();
        Vec3d vec3d = ctx.getHitPos();
        double d = vec3d.x - (double)blockPos.getX();
        double e = vec3d.z - (double)blockPos.getZ();
        return j < 0 && e < 0.5 || j > 0 && e > 0.5 || k < 0 && d > 0.5 || k > 0 && d < 0.5 ? DoorHinge.RIGHT : DoorHinge.LEFT;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!this.blockSetType.canOpenByHand()) {
            return ActionResult.PASS;
        }
        state = (BlockState)state.cycle((Property)OPEN);
        world.setBlockState(pos, state, 10);
        this.playOpenCloseSound((Entity)player, world, pos, ((Boolean)state.get((Property)OPEN)).booleanValue());
        world.emitGameEvent((Entity)player, (RegistryEntry)(this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pos);
        return ActionResult.SUCCESS;
    }

    public boolean isOpen(BlockState state) {
        return (Boolean)state.get((Property)OPEN);
    }

    public void setOpen(@Nullable Entity entity, World world, BlockState state, BlockPos pos, boolean open) {
        if (!state.isOf((Block)this) || (Boolean)state.get((Property)OPEN) == open) {
            return;
        }
        world.setBlockState(pos, (BlockState)state.with((Property)OPEN, (Comparable)Boolean.valueOf(open)), 10);
        this.playOpenCloseSound(entity, world, pos, open);
        world.emitGameEvent(entity, (RegistryEntry)(open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pos);
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        boolean bl;
        boolean bl2 = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.offset(state.get((Property)HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN)) ? true : (bl = false);
        if (!this.getDefaultState().isOf(sourceBlock) && bl != (Boolean)state.get((Property)POWERED)) {
            if (bl != (Boolean)state.get((Property)OPEN)) {
                this.playOpenCloseSound(null, world, pos, bl);
                world.emitGameEvent(null, (RegistryEntry)(bl ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE), pos);
            }
            world.setBlockState(pos, (BlockState)((BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(bl))).with((Property)OPEN, (Comparable)Boolean.valueOf(bl)), 2);
        }
    }

    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        if (state.get((Property)HALF) == DoubleBlockHalf.LOWER) {
            return blockState.isSideSolidFullSquare((BlockView)world, blockPos, Direction.UP);
        }
        return blockState.isOf((Block)this);
    }

    private void playOpenCloseSound(@Nullable Entity entity, World world, BlockPos pos, boolean open) {
        world.playSound(entity, pos, open ? this.blockSetType.doorOpen() : this.blockSetType.doorClose(), SoundCategory.BLOCKS, 1.0f, world.getRandom().nextFloat() * 0.1f + 0.9f);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        if (mirror == BlockMirror.NONE) {
            return state;
        }
        return (BlockState)state.rotate(mirror.getRotation((Direction)state.get((Property)FACING))).cycle((Property)HINGE);
    }

    protected long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode((int)pos.getX(), (int)pos.down(state.get((Property)HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), (int)pos.getZ());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HALF, FACING, OPEN, HINGE, POWERED});
    }

    public static boolean canOpenByHand(World world, BlockPos pos) {
        return DoorBlock.canOpenByHand((BlockState)world.getBlockState(pos));
    }

    public static boolean canOpenByHand(BlockState state) {
        DoorBlock doorBlock;
        Block block = state.getBlock();
        return block instanceof DoorBlock && (doorBlock = (DoorBlock)block).getBlockSetType().canOpenByHand();
    }
}

