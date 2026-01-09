package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
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
import net.minecraft.world.block.WireOrientation;

public class DetectorRailBlock extends AbstractRailBlock {
   public static final MapCodec CODEC = createCodec(DetectorRailBlock::new);
   public static final EnumProperty SHAPE;
   public static final BooleanProperty POWERED;
   private static final int SCHEDULED_TICK_DELAY = 20;

   public MapCodec getCodec() {
      return CODEC;
   }

   public DetectorRailBlock(AbstractBlock.Settings settings) {
      super(true, settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false)).with(SHAPE, RailShape.NORTH_SOUTH)).with(WATERLOGGED, false));
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (!world.isClient) {
         if (!(Boolean)state.get(POWERED)) {
            this.updatePoweredStatus(world, pos, state);
         }
      }
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((Boolean)state.get(POWERED)) {
         this.updatePoweredStatus(world, pos, state);
      }
   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(POWERED) ? 15 : 0;
   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      if (!(Boolean)state.get(POWERED)) {
         return 0;
      } else {
         return direction == Direction.UP ? 15 : 0;
      }
   }

   private void updatePoweredStatus(World world, BlockPos pos, BlockState state) {
      if (this.canPlaceAt(state, world, pos)) {
         boolean bl = (Boolean)state.get(POWERED);
         boolean bl2 = false;
         List list = this.getCarts(world, pos, AbstractMinecartEntity.class, (entity) -> {
            return true;
         });
         if (!list.isEmpty()) {
            bl2 = true;
         }

         BlockState blockState;
         if (bl2 && !bl) {
            blockState = (BlockState)state.with(POWERED, true);
            world.setBlockState(pos, blockState, 3);
            this.updateNearbyRails(world, pos, blockState, true);
            world.updateNeighbors(pos, this);
            world.updateNeighbors(pos.down(), this);
            world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
         }

         if (!bl2 && bl) {
            blockState = (BlockState)state.with(POWERED, false);
            world.setBlockState(pos, blockState, 3);
            this.updateNearbyRails(world, pos, blockState, false);
            world.updateNeighbors(pos, this);
            world.updateNeighbors(pos.down(), this);
            world.scheduleBlockRerenderIfNeeded(pos, state, blockState);
         }

         if (bl2) {
            world.scheduleBlockTick(pos, this, 20);
         }

         world.updateComparators(pos, this);
      }
   }

   protected void updateNearbyRails(World world, BlockPos pos, BlockState state, boolean unpowering) {
      RailPlacementHelper railPlacementHelper = new RailPlacementHelper(world, pos, state);
      List list = railPlacementHelper.getNeighbors();
      Iterator var7 = list.iterator();

      while(var7.hasNext()) {
         BlockPos blockPos = (BlockPos)var7.next();
         BlockState blockState = world.getBlockState(blockPos);
         world.updateNeighbor(blockState, blockPos, blockState.getBlock(), (WireOrientation)null, false);
      }

   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!oldState.isOf(state.getBlock())) {
         BlockState blockState = this.updateCurves(state, world, pos, notify);
         this.updatePoweredStatus(world, pos, blockState);
      }
   }

   public Property getShapeProperty() {
      return SHAPE;
   }

   protected boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      if ((Boolean)state.get(POWERED)) {
         List list = this.getCarts(world, pos, CommandBlockMinecartEntity.class, (cart) -> {
            return true;
         });
         if (!list.isEmpty()) {
            return ((CommandBlockMinecartEntity)list.get(0)).getCommandExecutor().getSuccessCount();
         }

         List list2 = this.getCarts(world, pos, AbstractMinecartEntity.class, EntityPredicates.VALID_INVENTORIES);
         if (!list2.isEmpty()) {
            return ScreenHandler.calculateComparatorOutput((Inventory)list2.get(0));
         }
      }

      return 0;
   }

   private List getCarts(World world, BlockPos pos, Class entityClass, Predicate entityPredicate) {
      return world.getEntitiesByClass(entityClass, this.getCartDetectionBox(pos), entityPredicate);
   }

   private Box getCartDetectionBox(BlockPos pos) {
      double d = 0.2;
      return new Box((double)pos.getX() + 0.2, (double)pos.getY(), (double)pos.getZ() + 0.2, (double)(pos.getX() + 1) - 0.2, (double)(pos.getY() + 1) - 0.2, (double)(pos.getZ() + 1) - 0.2);
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      RailShape railShape = (RailShape)state.get(SHAPE);
      RailShape railShape2 = this.rotateShape(railShape, rotation);
      return (BlockState)state.with(SHAPE, railShape2);
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      RailShape railShape = (RailShape)state.get(SHAPE);
      RailShape railShape2 = this.mirrorShape(railShape, mirror);
      return (BlockState)state.with(SHAPE, railShape2);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(SHAPE, POWERED, WATERLOGGED);
   }

   static {
      SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
      POWERED = Properties.POWERED;
   }
}
