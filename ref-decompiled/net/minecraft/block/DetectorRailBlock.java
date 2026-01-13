/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractRailBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.DetectorRailBlock
 *  net.minecraft.block.RailPlacementHelper
 *  net.minecraft.block.enums.RailShape
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.vehicle.AbstractMinecartEntity
 *  net.minecraft.entity.vehicle.CommandBlockMinecartEntity
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.predicate.entity.EntityPredicates
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailPlacementHelper;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class DetectorRailBlock
extends AbstractRailBlock {
    public static final MapCodec<DetectorRailBlock> CODEC = DetectorRailBlock.createCodec(DetectorRailBlock::new);
    public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = Properties.POWERED;
    private static final int SCHEDULED_TICK_DELAY = 20;

    public MapCodec<DetectorRailBlock> getCodec() {
        return CODEC;
    }

    public DetectorRailBlock(AbstractBlock.Settings settings) {
        super(true, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)POWERED, (Comparable)Boolean.valueOf(false))).with((Property)SHAPE, (Comparable)RailShape.NORTH_SOUTH)).with((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (world.isClient()) {
            return;
        }
        if (((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return;
        }
        this.updatePoweredStatus(world, pos, state);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return;
        }
        this.updatePoweredStatus((World)world, pos, state);
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Boolean)state.get((Property)POWERED) != false ? 15 : 0;
    }

    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!((Boolean)state.get((Property)POWERED)).booleanValue()) {
            return 0;
        }
        return direction == Direction.UP ? 15 : 0;
    }

    private void updatePoweredStatus(World world, BlockPos pos, BlockState state) {
        BlockState blockState;
        if (!this.canPlaceAt(state, (WorldView)world, pos)) {
            return;
        }
        boolean bl = (Boolean)state.get((Property)POWERED);
        boolean bl2 = false;
        List list = this.getCarts(world, pos, AbstractMinecartEntity.class, entity -> true);
        if (!list.isEmpty()) {
            bl2 = true;
        }
        if (bl2 && !bl) {
            blockState = (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(true));
            world.setBlockState(pos, blockState, 3);
            this.updateNearbyRails(world, pos, blockState, true);
            world.updateNeighbors(pos, (Block)this);
            world.updateNeighbors(pos.down(), (Block)this);
            world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
        }
        if (!bl2 && bl) {
            blockState = (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(false));
            world.setBlockState(pos, blockState, 3);
            this.updateNearbyRails(world, pos, blockState, false);
            world.updateNeighbors(pos, (Block)this);
            world.updateNeighbors(pos.down(), (Block)this);
            world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
        }
        if (bl2) {
            world.scheduleBlockTick(pos, (Block)this, 20);
        }
        world.updateComparators(pos, (Block)this);
    }

    protected void updateNearbyRails(World world, BlockPos pos, BlockState state, boolean unpowering) {
        RailPlacementHelper railPlacementHelper = new RailPlacementHelper(world, pos, state);
        List list = railPlacementHelper.getNeighbors();
        for (BlockPos blockPos : list) {
            BlockState blockState = world.getBlockState(blockPos);
            world.updateNeighbor(blockState, blockPos, blockState.getBlock(), null, false);
        }
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        BlockState blockState = this.updateCurves(state, world, pos, notify);
        this.updatePoweredStatus(world, pos, blockState);
    }

    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos, Direction direction) {
        if (((Boolean)state.get((Property)POWERED)).booleanValue()) {
            List list = this.getCarts(world, pos, CommandBlockMinecartEntity.class, cart -> true);
            if (!list.isEmpty()) {
                return ((CommandBlockMinecartEntity)list.get(0)).getCommandExecutor().getSuccessCount();
            }
            List list2 = this.getCarts(world, pos, AbstractMinecartEntity.class, EntityPredicates.VALID_INVENTORIES);
            if (!list2.isEmpty()) {
                return ScreenHandler.calculateComparatorOutput((Inventory)((Inventory)list2.get(0)));
            }
        }
        return 0;
    }

    private <T extends AbstractMinecartEntity> List<T> getCarts(World world, BlockPos pos, Class<T> entityClass, Predicate<Entity> entityPredicate) {
        return world.getEntitiesByClass(entityClass, this.getCartDetectionBox(pos), entityPredicate);
    }

    private Box getCartDetectionBox(BlockPos pos) {
        double d = 0.2;
        return new Box((double)pos.getX() + 0.2, (double)pos.getY(), (double)pos.getZ() + 0.2, (double)(pos.getX() + 1) - 0.2, (double)(pos.getY() + 1) - 0.2, (double)(pos.getZ() + 1) - 0.2);
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        RailShape railShape = (RailShape)state.get((Property)SHAPE);
        RailShape railShape2 = this.rotateShape(railShape, rotation);
        return (BlockState)state.with((Property)SHAPE, (Comparable)railShape2);
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        RailShape railShape = (RailShape)state.get((Property)SHAPE);
        RailShape railShape2 = this.mirrorShape(railShape, mirror);
        return (BlockState)state.with((Property)SHAPE, (Comparable)railShape2);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{SHAPE, POWERED, WATERLOGGED});
    }
}

