package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;

public class TripwireBlock extends Block {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Registries.BLOCK.getCodec().fieldOf("hook").forGetter((block) -> {
         return block.hookBlock;
      }), createSettingsCodec()).apply(instance, TripwireBlock::new);
   });
   public static final BooleanProperty POWERED;
   public static final BooleanProperty ATTACHED;
   public static final BooleanProperty DISARMED;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   private static final Map FACING_PROPERTIES;
   private static final VoxelShape ATTACHED_SHAPE;
   private static final VoxelShape UNATTACHED_SHAPE;
   private static final int SCHEDULED_TICK_DELAY = 10;
   private final Block hookBlock;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TripwireBlock(Block hookBlock, AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false)).with(ATTACHED, false)).with(DISARMED, false)).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false));
      this.hookBlock = hookBlock;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (Boolean)state.get(ATTACHED) ? ATTACHED_SHAPE : UNATTACHED_SHAPE;
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockView blockView = ctx.getWorld();
      BlockPos blockPos = ctx.getBlockPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.getDefaultState().with(NORTH, this.shouldConnectTo(blockView.getBlockState(blockPos.north()), Direction.NORTH))).with(EAST, this.shouldConnectTo(blockView.getBlockState(blockPos.east()), Direction.EAST))).with(SOUTH, this.shouldConnectTo(blockView.getBlockState(blockPos.south()), Direction.SOUTH))).with(WEST, this.shouldConnectTo(blockView.getBlockState(blockPos.west()), Direction.WEST));
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction.getAxis().isHorizontal() ? (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), this.shouldConnectTo(neighborState, direction)) : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!oldState.isOf(state.getBlock())) {
         this.update(world, pos, state);
      }
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      if (!moved) {
         this.update(world, pos, (BlockState)state.with(POWERED, true));
      }

   }

   public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!world.isClient && !player.getMainHandStack().isEmpty() && player.getMainHandStack().isOf(Items.SHEARS)) {
         world.setBlockState(pos, (BlockState)state.with(DISARMED, true), 260);
         world.emitGameEvent(player, GameEvent.SHEAR, pos);
      }

      return super.onBreak(world, pos, state, player);
   }

   private void update(World world, BlockPos pos, BlockState state) {
      Direction[] var4 = new Direction[]{Direction.SOUTH, Direction.WEST};
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction direction = var4[var6];

         for(int i = 1; i < 42; ++i) {
            BlockPos blockPos = pos.offset(direction, i);
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(this.hookBlock)) {
               if (blockState.get(TripwireHookBlock.FACING) == direction.getOpposite()) {
                  TripwireHookBlock.update(world, blockPos, blockState, false, true, i, state);
               }
               break;
            }

            if (!blockState.isOf(this)) {
               break;
            }
         }
      }

   }

   protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
      return state.getOutlineShape(world, pos);
   }

   protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (!world.isClient) {
         if (!(Boolean)state.get(POWERED)) {
            this.updatePowered(world, pos, List.of(entity));
         }
      }
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      if ((Boolean)world.getBlockState(pos).get(POWERED)) {
         this.updatePowered(world, pos);
      }
   }

   private void updatePowered(World world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      List list = world.getOtherEntities((Entity)null, blockState.getOutlineShape(world, pos).getBoundingBox().offset(pos));
      this.updatePowered(world, pos, list);
   }

   private void updatePowered(World world, BlockPos pos, List entities) {
      BlockState blockState = world.getBlockState(pos);
      boolean bl = (Boolean)blockState.get(POWERED);
      boolean bl2 = false;
      if (!entities.isEmpty()) {
         Iterator var7 = entities.iterator();

         while(var7.hasNext()) {
            Entity entity = (Entity)var7.next();
            if (!entity.canAvoidTraps()) {
               bl2 = true;
               break;
            }
         }
      }

      if (bl2 != bl) {
         blockState = (BlockState)blockState.with(POWERED, bl2);
         world.setBlockState(pos, blockState, 3);
         this.update(world, pos, blockState);
      }

      if (bl2) {
         world.scheduleBlockTick(new BlockPos(pos), this, 10);
      }

   }

   public boolean shouldConnectTo(BlockState state, Direction facing) {
      if (state.isOf(this.hookBlock)) {
         return state.get(TripwireHookBlock.FACING) == facing.getOpposite();
      } else {
         return state.isOf(this);
      }
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      switch (rotation) {
         case CLOCKWISE_180:
            return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, (Boolean)state.get(SOUTH))).with(EAST, (Boolean)state.get(WEST))).with(SOUTH, (Boolean)state.get(NORTH))).with(WEST, (Boolean)state.get(EAST));
         case COUNTERCLOCKWISE_90:
            return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, (Boolean)state.get(EAST))).with(EAST, (Boolean)state.get(SOUTH))).with(SOUTH, (Boolean)state.get(WEST))).with(WEST, (Boolean)state.get(NORTH));
         case CLOCKWISE_90:
            return (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, (Boolean)state.get(WEST))).with(EAST, (Boolean)state.get(NORTH))).with(SOUTH, (Boolean)state.get(EAST))).with(WEST, (Boolean)state.get(SOUTH));
         default:
            return state;
      }
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      switch (mirror) {
         case LEFT_RIGHT:
            return (BlockState)((BlockState)state.with(NORTH, (Boolean)state.get(SOUTH))).with(SOUTH, (Boolean)state.get(NORTH));
         case FRONT_BACK:
            return (BlockState)((BlockState)state.with(EAST, (Boolean)state.get(WEST))).with(WEST, (Boolean)state.get(EAST));
         default:
            return super.mirror(state, mirror);
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }

   static {
      POWERED = Properties.POWERED;
      ATTACHED = Properties.ATTACHED;
      DISARMED = Properties.DISARMED;
      NORTH = ConnectingBlock.NORTH;
      EAST = ConnectingBlock.EAST;
      SOUTH = ConnectingBlock.SOUTH;
      WEST = ConnectingBlock.WEST;
      FACING_PROPERTIES = HorizontalConnectingBlock.FACING_PROPERTIES;
      ATTACHED_SHAPE = Block.createColumnShape(16.0, 1.0, 2.5);
      UNATTACHED_SHAPE = Block.createColumnShape(16.0, 0.0, 8.0);
   }
}
