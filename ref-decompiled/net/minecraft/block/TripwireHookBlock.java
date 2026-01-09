package net.minecraft.block;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class TripwireHookBlock extends Block {
   public static final MapCodec CODEC = createCodec(TripwireHookBlock::new);
   public static final EnumProperty FACING;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty ATTACHED;
   protected static final int field_31268 = 1;
   protected static final int field_31269 = 42;
   private static final int SCHEDULED_TICK_DELAY = 10;
   private static final Map SHAPES_BY_DIRECTION;

   public MapCodec getCodec() {
      return CODEC;
   }

   public TripwireHookBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(POWERED, false)).with(ATTACHED, false));
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)SHAPES_BY_DIRECTION.get(state.get(FACING));
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      Direction direction = (Direction)state.get(FACING);
      BlockPos blockPos = pos.offset(direction.getOpposite());
      BlockState blockState = world.getBlockState(blockPos);
      return direction.getAxis().isHorizontal() && blockState.isSideSolidFullSquare(world, blockPos, direction);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      return direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState blockState = (BlockState)((BlockState)this.getDefaultState().with(POWERED, false)).with(ATTACHED, false);
      WorldView worldView = ctx.getWorld();
      BlockPos blockPos = ctx.getBlockPos();
      Direction[] directions = ctx.getPlacementDirections();
      Direction[] var6 = directions;
      int var7 = directions.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction direction = var6[var8];
         if (direction.getAxis().isHorizontal()) {
            Direction direction2 = direction.getOpposite();
            blockState = (BlockState)blockState.with(FACING, direction2);
            if (blockState.canPlaceAt(worldView, blockPos)) {
               return blockState;
            }
         }
      }

      return null;
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
      update(world, pos, state, false, false, -1, (BlockState)null);
   }

   public static void update(World world, BlockPos pos, BlockState state, boolean bl, boolean bl2, int i, @Nullable BlockState blockState) {
      Optional optional = state.getOrEmpty(FACING);
      if (optional.isPresent()) {
         Direction direction = (Direction)optional.get();
         boolean bl3 = (Boolean)state.getOrEmpty(ATTACHED).orElse(false);
         boolean bl4 = (Boolean)state.getOrEmpty(POWERED).orElse(false);
         Block block = state.getBlock();
         boolean bl5 = !bl;
         boolean bl6 = false;
         int j = 0;
         BlockState[] blockStates = new BlockState[42];

         BlockPos blockPos;
         for(int k = 1; k < 42; ++k) {
            blockPos = pos.offset(direction, k);
            BlockState blockState2 = world.getBlockState(blockPos);
            if (blockState2.isOf(Blocks.TRIPWIRE_HOOK)) {
               if (blockState2.get(FACING) == direction.getOpposite()) {
                  j = k;
               }
               break;
            }

            if (!blockState2.isOf(Blocks.TRIPWIRE) && k != i) {
               blockStates[k] = null;
               bl5 = false;
            } else {
               if (k == i) {
                  blockState2 = (BlockState)MoreObjects.firstNonNull(blockState, blockState2);
               }

               boolean bl7 = !(Boolean)blockState2.get(TripwireBlock.DISARMED);
               boolean bl8 = (Boolean)blockState2.get(TripwireBlock.POWERED);
               bl6 |= bl7 && bl8;
               blockStates[k] = blockState2;
               if (k == i) {
                  world.scheduleBlockTick(pos, block, 10);
                  bl5 &= bl7;
               }
            }
         }

         bl5 &= j > 1;
         bl6 &= bl5;
         BlockState blockState3 = (BlockState)((BlockState)block.getDefaultState().withIfExists(ATTACHED, bl5)).withIfExists(POWERED, bl6);
         if (j > 0) {
            blockPos = pos.offset(direction, j);
            Direction direction2 = direction.getOpposite();
            world.setBlockState(blockPos, (BlockState)blockState3.with(FACING, direction2), 3);
            updateNeighborsOnAxis(block, world, blockPos, direction2);
            playSound(world, blockPos, bl5, bl6, bl3, bl4);
         }

         playSound(world, pos, bl5, bl6, bl3, bl4);
         if (!bl) {
            world.setBlockState(pos, (BlockState)blockState3.with(FACING, direction), 3);
            if (bl2) {
               updateNeighborsOnAxis(block, world, pos, direction);
            }
         }

         if (bl3 != bl5) {
            for(int l = 1; l < j; ++l) {
               BlockPos blockPos2 = pos.offset(direction, l);
               BlockState blockState4 = blockStates[l];
               if (blockState4 != null) {
                  BlockState blockState5 = world.getBlockState(blockPos2);
                  if (blockState5.isOf(Blocks.TRIPWIRE) || blockState5.isOf(Blocks.TRIPWIRE_HOOK)) {
                     world.setBlockState(blockPos2, (BlockState)blockState4.withIfExists(ATTACHED, bl5), 3);
                  }
               }
            }
         }

      }
   }

   protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      update(world, pos, state, false, true, -1, (BlockState)null);
   }

   private static void playSound(World world, BlockPos pos, boolean attached, boolean on, boolean detached, boolean off) {
      if (on && !off) {
         world.playSound((Entity)null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
         world.emitGameEvent((Entity)null, GameEvent.BLOCK_ACTIVATE, pos);
      } else if (!on && off) {
         world.playSound((Entity)null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
         world.emitGameEvent((Entity)null, GameEvent.BLOCK_DEACTIVATE, pos);
      } else if (attached && !detached) {
         world.playSound((Entity)null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
         world.emitGameEvent((Entity)null, GameEvent.BLOCK_ATTACH, pos);
      } else if (!attached && detached) {
         world.playSound((Entity)null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
         world.emitGameEvent((Entity)null, GameEvent.BLOCK_DETACH, pos);
      }

   }

   private static void updateNeighborsOnAxis(Block block, World world, BlockPos pos, Direction direction) {
      Direction direction2 = direction.getOpposite();
      WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, direction2, Direction.UP);
      world.updateNeighborsAlways(pos, block, wireOrientation);
      world.updateNeighborsAlways(pos.offset(direction2), block, wireOrientation);
   }

   protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
      if (!moved) {
         boolean bl = (Boolean)state.get(ATTACHED);
         boolean bl2 = (Boolean)state.get(POWERED);
         if (bl || bl2) {
            update(world, pos, state, true, false, -1, (BlockState)null);
         }

         if (bl2) {
            updateNeighborsOnAxis(this, world, pos, (Direction)state.get(FACING));
         }

      }
   }

   protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      return (Boolean)state.get(POWERED) ? 15 : 0;
   }

   protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      if (!(Boolean)state.get(POWERED)) {
         return 0;
      } else {
         return state.get(FACING) == direction ? 15 : 0;
      }
   }

   protected boolean emitsRedstonePower(BlockState state) {
      return true;
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, POWERED, ATTACHED);
   }

   static {
      FACING = HorizontalFacingBlock.FACING;
      POWERED = Properties.POWERED;
      ATTACHED = Properties.ATTACHED;
      SHAPES_BY_DIRECTION = VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidZShape(6.0, 0.0, 10.0, 10.0, 16.0));
   }
}
