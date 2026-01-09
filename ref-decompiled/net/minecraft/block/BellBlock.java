package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.BiConsumer;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
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
import org.jetbrains.annotations.Nullable;

public class BellBlock extends BlockWithEntity {
   public static final MapCodec CODEC = createCodec(BellBlock::new);
   public static final EnumProperty FACING;
   public static final EnumProperty ATTACHMENT;
   public static final BooleanProperty POWERED;
   private static final VoxelShape BELL_SHAPE;
   private static final VoxelShape CEILING_SHAPE;
   private static final Map FLOOR_SHAPES;
   private static final Map DOUBLE_WALL_SHAPES;
   private static final Map SINGLE_WALL_SHAPES;
   public static final int field_31014 = 1;

   public MapCodec getCodec() {
      return CODEC;
   }

   public BellBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(ATTACHMENT, Attachment.FLOOR)).with(POWERED, false));
   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      boolean bl = world.isReceivingRedstonePower(pos);
      if (bl != (Boolean)state.get(POWERED)) {
         if (bl) {
            this.ring(world, pos, (Direction)null);
         }

         world.setBlockState(pos, (BlockState)state.with(POWERED, bl), 3);
      }

   }

   protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
      Entity entity = projectile.getOwner();
      PlayerEntity var10000;
      if (entity instanceof PlayerEntity playerEntity) {
         var10000 = playerEntity;
      } else {
         var10000 = null;
      }

      PlayerEntity playerEntity2 = var10000;
      this.ring(world, state, hit, playerEntity2, true);
   }

   protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
      return (ActionResult)(this.ring(world, state, hit, player, true) ? ActionResult.SUCCESS : ActionResult.PASS);
   }

   public boolean ring(World world, BlockState state, BlockHitResult hitResult, @Nullable PlayerEntity player, boolean checkHitPos) {
      Direction direction = hitResult.getSide();
      BlockPos blockPos = hitResult.getBlockPos();
      boolean bl = !checkHitPos || this.isPointOnBell(state, direction, hitResult.getPos().y - (double)blockPos.getY());
      if (bl) {
         boolean bl2 = this.ring(player, world, blockPos, direction);
         if (bl2 && player != null) {
            player.incrementStat(Stats.BELL_RING);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isPointOnBell(BlockState state, Direction side, double y) {
      if (side.getAxis() != Direction.Axis.Y && !(y > 0.8123999834060669)) {
         Direction direction = (Direction)state.get(FACING);
         Attachment attachment = (Attachment)state.get(ATTACHMENT);
         switch (attachment) {
            case FLOOR:
               return direction.getAxis() == side.getAxis();
            case SINGLE_WALL:
            case DOUBLE_WALL:
               return direction.getAxis() != side.getAxis();
            case CEILING:
               return true;
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   public boolean ring(World world, BlockPos pos, @Nullable Direction direction) {
      return this.ring((Entity)null, world, pos, direction);
   }

   public boolean ring(@Nullable Entity entity, World world, BlockPos pos, @Nullable Direction direction) {
      BlockEntity blockEntity = world.getBlockEntity(pos);
      if (!world.isClient && blockEntity instanceof BellBlockEntity) {
         if (direction == null) {
            direction = (Direction)world.getBlockState(pos).get(FACING);
         }

         ((BellBlockEntity)blockEntity).activate(direction);
         world.playSound((Entity)null, pos, SoundEvents.BLOCK_BELL_USE, SoundCategory.BLOCKS, 2.0F, 1.0F);
         world.emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShape getShape(BlockState state) {
      Direction direction = (Direction)state.get(FACING);
      VoxelShape var10000;
      switch ((Attachment)state.get(ATTACHMENT)) {
         case FLOOR:
            var10000 = (VoxelShape)FLOOR_SHAPES.get(direction.getAxis());
            break;
         case SINGLE_WALL:
            var10000 = (VoxelShape)SINGLE_WALL_SHAPES.get(direction);
            break;
         case DOUBLE_WALL:
            var10000 = (VoxelShape)DOUBLE_WALL_SHAPES.get(direction.getAxis());
            break;
         case CEILING:
            var10000 = CEILING_SHAPE;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return this.getShape(state);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return this.getShape(state);
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      Direction direction = ctx.getSide();
      BlockPos blockPos = ctx.getBlockPos();
      World world = ctx.getWorld();
      Direction.Axis axis = direction.getAxis();
      BlockState blockState;
      if (axis == Direction.Axis.Y) {
         blockState = (BlockState)((BlockState)this.getDefaultState().with(ATTACHMENT, direction == Direction.DOWN ? Attachment.CEILING : Attachment.FLOOR)).with(FACING, ctx.getHorizontalPlayerFacing());
         if (blockState.canPlaceAt(ctx.getWorld(), blockPos)) {
            return blockState;
         }
      } else {
         boolean bl = axis == Direction.Axis.X && world.getBlockState(blockPos.west()).isSideSolidFullSquare(world, blockPos.west(), Direction.EAST) && world.getBlockState(blockPos.east()).isSideSolidFullSquare(world, blockPos.east(), Direction.WEST) || axis == Direction.Axis.Z && world.getBlockState(blockPos.north()).isSideSolidFullSquare(world, blockPos.north(), Direction.SOUTH) && world.getBlockState(blockPos.south()).isSideSolidFullSquare(world, blockPos.south(), Direction.NORTH);
         blockState = (BlockState)((BlockState)this.getDefaultState().with(FACING, direction.getOpposite())).with(ATTACHMENT, bl ? Attachment.DOUBLE_WALL : Attachment.SINGLE_WALL);
         if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
            return blockState;
         }

         boolean bl2 = world.getBlockState(blockPos.down()).isSideSolidFullSquare(world, blockPos.down(), Direction.UP);
         blockState = (BlockState)blockState.with(ATTACHMENT, bl2 ? Attachment.FLOOR : Attachment.CEILING);
         if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
            return blockState;
         }
      }

      return null;
   }

   protected void onExploded(BlockState state, ServerWorld world, BlockPos pos, Explosion explosion, BiConsumer stackMerger) {
      if (explosion.canTriggerBlocks()) {
         this.ring(world, pos, (Direction)null);
      }

      super.onExploded(state, world, pos, explosion, stackMerger);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      Attachment attachment = (Attachment)state.get(ATTACHMENT);
      Direction direction2 = getPlacementSide(state).getOpposite();
      if (direction2 == direction && !state.canPlaceAt(world, pos) && attachment != Attachment.DOUBLE_WALL) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (direction.getAxis() == ((Direction)state.get(FACING)).getAxis()) {
            if (attachment == Attachment.DOUBLE_WALL && !neighborState.isSideSolidFullSquare(world, neighborPos, direction)) {
               return (BlockState)((BlockState)state.with(ATTACHMENT, Attachment.SINGLE_WALL)).with(FACING, direction.getOpposite());
            }

            if (attachment == Attachment.SINGLE_WALL && direction2.getOpposite() == direction && neighborState.isSideSolidFullSquare(world, neighborPos, (Direction)state.get(FACING))) {
               return (BlockState)state.with(ATTACHMENT, Attachment.DOUBLE_WALL);
            }
         }

         return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
      }
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      Direction direction = getPlacementSide(state).getOpposite();
      return direction == Direction.UP ? Block.sideCoversSmallSquare(world, pos.up(), Direction.DOWN) : WallMountedBlock.canPlaceAt(world, pos, direction);
   }

   private static Direction getPlacementSide(BlockState state) {
      switch ((Attachment)state.get(ATTACHMENT)) {
         case FLOOR:
            return Direction.UP;
         case CEILING:
            return Direction.DOWN;
         default:
            return ((Direction)state.get(FACING)).getOpposite();
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, ATTACHMENT, POWERED);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new BellBlockEntity(pos, state);
   }

   @Nullable
   public BlockEntityTicker getTicker(World world, BlockState state, BlockEntityType type) {
      return validateTicker(type, BlockEntityType.BELL, world.isClient ? BellBlockEntity::clientTick : BellBlockEntity::serverTick);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   public BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   public BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      ATTACHMENT = Properties.ATTACHMENT;
      POWERED = Properties.POWERED;
      BELL_SHAPE = VoxelShapes.union(Block.createColumnShape(6.0, 6.0, 13.0), Block.createColumnShape(8.0, 4.0, 6.0));
      CEILING_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createColumnShape(2.0, 13.0, 16.0));
      FLOOR_SHAPES = VoxelShapes.createHorizontalAxisShapeMap(Block.createCuboidShape(16.0, 16.0, 8.0));
      DOUBLE_WALL_SHAPES = VoxelShapes.createHorizontalAxisShapeMap(VoxelShapes.union(BELL_SHAPE, Block.createColumnShape(2.0, 16.0, 13.0, 15.0)));
      SINGLE_WALL_SHAPES = VoxelShapes.createHorizontalFacingShapeMap(VoxelShapes.union(BELL_SHAPE, Block.createCuboidZShape(2.0, 13.0, 15.0, 0.0, 13.0)));
   }
}
