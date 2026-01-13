/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.HopperBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.HopperBlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.ItemScatterer
 *  net.minecraft.util.function.BooleanBiFunction
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class HopperBlock
extends BlockWithEntity {
    public static final MapCodec<HopperBlock> CODEC = HopperBlock.createCodec(HopperBlock::new);
    public static final EnumProperty<Direction> FACING = Properties.HOPPER_FACING;
    public static final BooleanProperty ENABLED = Properties.ENABLED;
    private final Function<BlockState, VoxelShape> shapeFunction;
    private final Map<Direction, VoxelShape> shapesByDirection;

    public MapCodec<HopperBlock> getCodec() {
        return CODEC;
    }

    public HopperBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.DOWN)).with((Property)ENABLED, (Comparable)Boolean.valueOf(true)));
        VoxelShape voxelShape = Block.createColumnShape((double)12.0, (double)11.0, (double)16.0);
        this.shapeFunction = this.createShapeFunction(voxelShape);
        this.shapesByDirection = ImmutableMap.builderWithExpectedSize((int)5).putAll(VoxelShapes.createHorizontalFacingShapeMap((VoxelShape)VoxelShapes.union((VoxelShape)voxelShape, (VoxelShape)Block.createCuboidZShape((double)4.0, (double)8.0, (double)10.0, (double)0.0, (double)4.0)))).put((Object)Direction.DOWN, (Object)voxelShape).build();
    }

    private Function<BlockState, VoxelShape> createShapeFunction(VoxelShape shape) {
        VoxelShape voxelShape = VoxelShapes.union((VoxelShape)Block.createColumnShape((double)16.0, (double)10.0, (double)16.0), (VoxelShape)Block.createColumnShape((double)8.0, (double)4.0, (double)10.0));
        VoxelShape voxelShape2 = VoxelShapes.combineAndSimplify((VoxelShape)voxelShape, (VoxelShape)shape, (BooleanBiFunction)BooleanBiFunction.ONLY_FIRST);
        Map map = VoxelShapes.createFacingShapeMap((VoxelShape)Block.createCuboidZShape((double)4.0, (double)4.0, (double)8.0, (double)0.0, (double)8.0), (Vec3d)new Vec3d(8.0, 6.0, 8.0).multiply(0.0625));
        return this.createShapeFunction(state -> VoxelShapes.union((VoxelShape)voxelShape2, (VoxelShape)VoxelShapes.combineAndSimplify((VoxelShape)((VoxelShape)map.get(state.get((Property)FACING))), (VoxelShape)VoxelShapes.fullCube(), (BooleanBiFunction)BooleanBiFunction.AND)), new Property[]{ENABLED});
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.shapeFunction.apply(state);
    }

    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return (VoxelShape)this.shapesByDirection.get(state.get((Property)FACING));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide().getOpposite();
        return (BlockState)((BlockState)this.getDefaultState().with((Property)FACING, (Comparable)(direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction))).with((Property)ENABLED, (Comparable)Boolean.valueOf(true));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HopperBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : HopperBlock.validateTicker(type, (BlockEntityType)BlockEntityType.HOPPER, HopperBlockEntity::serverTick);
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        this.updateEnabled(world, pos, state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (!world.isClient() && (blockEntity = world.getBlockEntity(pos)) instanceof HopperBlockEntity) {
            HopperBlockEntity hopperBlockEntity = (HopperBlockEntity)blockEntity;
            player.openHandledScreen((NamedScreenHandlerFactory)hopperBlockEntity);
            player.incrementStat(Stats.INSPECT_HOPPER);
        }
        return ActionResult.SUCCESS;
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(World world, BlockPos pos, BlockState state) {
        boolean bl;
        boolean bl2 = bl = !world.isReceivingRedstonePower(pos);
        if (bl != (Boolean)state.get((Property)ENABLED)) {
            world.setBlockState(pos, (BlockState)state.with((Property)ENABLED, (Comparable)Boolean.valueOf(bl)), 2);
        }
    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced((BlockState)state, (World)world, (BlockPos)pos);
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        return ScreenHandler.calculateComparatorOutput((BlockEntity)world.getBlockEntity(pos));
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, ENABLED});
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof HopperBlockEntity) {
            HopperBlockEntity.onEntityCollided((World)world, (BlockPos)pos, (BlockState)state, (Entity)entity, (HopperBlockEntity)((HopperBlockEntity)blockEntity));
        }
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

