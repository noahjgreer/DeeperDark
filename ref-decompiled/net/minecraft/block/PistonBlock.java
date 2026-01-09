package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import net.minecraft.world.block.OrientationHelper;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class PistonBlock extends FacingBlock {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.fieldOf("sticky").forGetter((block) -> {
         return block.sticky;
      }), createSettingsCodec()).apply(instance, PistonBlock::new);
   });
   public static final BooleanProperty EXTENDED;
   public static final int field_31373 = 0;
   public static final int field_31374 = 1;
   public static final int field_31375 = 2;
   public static final int field_31376 = 4;
   private static final Map EXTENDED_SHAPES_BY_DIRECTION;
   private final boolean sticky;

   public MapCodec getCodec() {
      return CODEC;
   }

   public PistonBlock(boolean sticky, AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH)).with(EXTENDED, false));
      this.sticky = sticky;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (Boolean)state.get(EXTENDED) ? (VoxelShape)EXTENDED_SHAPES_BY_DIRECTION.get(state.get(FACING)) : VoxelShapes.fullCube();
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
      if (!world.isClient) {
         this.tryMove(world, pos, state);
      }

   }

   protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
      if (!world.isClient) {
         this.tryMove(world, pos, state);
      }

   }

   protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
      if (!oldState.isOf(state.getBlock())) {
         if (!world.isClient && world.getBlockEntity(pos) == null) {
            this.tryMove(world, pos, state);
         }

      }
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return (BlockState)((BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite())).with(EXTENDED, false);
   }

   private void tryMove(World world, BlockPos pos, BlockState state) {
      Direction direction = (Direction)state.get(FACING);
      boolean bl = this.shouldExtend(world, pos, direction);
      if (bl && !(Boolean)state.get(EXTENDED)) {
         if ((new PistonHandler(world, pos, direction, true)).calculatePush()) {
            world.addSyncedBlockEvent(pos, this, 0, direction.getIndex());
         }
      } else if (!bl && (Boolean)state.get(EXTENDED)) {
         BlockPos blockPos = pos.offset((Direction)direction, 2);
         BlockState blockState = world.getBlockState(blockPos);
         int i = 1;
         if (blockState.isOf(Blocks.MOVING_PISTON) && blockState.get(FACING) == direction) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof PistonBlockEntity) {
               PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity;
               if (pistonBlockEntity.isExtending() && (pistonBlockEntity.getProgress(0.0F) < 0.5F || world.getTime() == pistonBlockEntity.getSavedWorldTime() || ((ServerWorld)world).isInBlockTick())) {
                  i = 2;
               }
            }
         }

         world.addSyncedBlockEvent(pos, this, i, direction.getIndex());
      }

   }

   private boolean shouldExtend(RedstoneView world, BlockPos pos, Direction pistonFace) {
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         Direction direction = var4[var6];
         if (direction != pistonFace && world.isEmittingRedstonePower(pos.offset(direction), direction)) {
            return true;
         }
      }

      if (world.isEmittingRedstonePower(pos, Direction.DOWN)) {
         return true;
      } else {
         BlockPos blockPos = pos.up();
         Direction[] var10 = Direction.values();
         var6 = var10.length;

         for(int var11 = 0; var11 < var6; ++var11) {
            Direction direction2 = var10[var11];
            if (direction2 != Direction.DOWN && world.isEmittingRedstonePower(blockPos.offset(direction2), direction2)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
      Direction direction = (Direction)state.get(FACING);
      BlockState blockState = (BlockState)state.with(EXTENDED, true);
      if (!world.isClient) {
         boolean bl = this.shouldExtend(world, pos, direction);
         if (bl && (type == 1 || type == 2)) {
            world.setBlockState(pos, blockState, 2);
            return false;
         }

         if (!bl && type == 0) {
            return false;
         }
      }

      if (type == 0) {
         if (!this.move(world, pos, direction, true)) {
            return false;
         }

         world.setBlockState(pos, blockState, 67);
         world.playSound((Entity)null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
         world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(blockState));
      } else if (type == 1 || type == 2) {
         BlockEntity blockEntity = world.getBlockEntity(pos.offset(direction));
         if (blockEntity instanceof PistonBlockEntity) {
            ((PistonBlockEntity)blockEntity).finish();
         }

         BlockState blockState2 = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, direction)).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
         world.setBlockState(pos, blockState2, 276);
         world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(pos, blockState2, (BlockState)this.getDefaultState().with(FACING, Direction.byIndex(data & 7)), direction, false, true));
         world.updateNeighbors(pos, blockState2.getBlock());
         blockState2.updateNeighbors(world, pos, 2);
         if (this.sticky) {
            BlockPos blockPos = pos.add(direction.getOffsetX() * 2, direction.getOffsetY() * 2, direction.getOffsetZ() * 2);
            BlockState blockState3 = world.getBlockState(blockPos);
            boolean bl2 = false;
            if (blockState3.isOf(Blocks.MOVING_PISTON)) {
               BlockEntity blockEntity2 = world.getBlockEntity(blockPos);
               if (blockEntity2 instanceof PistonBlockEntity) {
                  PistonBlockEntity pistonBlockEntity = (PistonBlockEntity)blockEntity2;
                  if (pistonBlockEntity.getFacing() == direction && pistonBlockEntity.isExtending()) {
                     pistonBlockEntity.finish();
                     bl2 = true;
                  }
               }
            }

            if (!bl2) {
               if (type != 1 || blockState3.isAir() || !isMovable(blockState3, world, blockPos, direction.getOpposite(), false, direction) || blockState3.getPistonBehavior() != PistonBehavior.NORMAL && !blockState3.isOf(Blocks.PISTON) && !blockState3.isOf(Blocks.STICKY_PISTON)) {
                  world.removeBlock(pos.offset(direction), false);
               } else {
                  this.move(world, pos, direction, false);
               }
            }
         } else {
            world.removeBlock(pos.offset(direction), false);
         }

         world.playSound((Entity)null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
         world.emitGameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Emitter.of(blockState2));
      }

      return true;
   }

   public static boolean isMovable(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonDir) {
      if (pos.getY() >= world.getBottomY() && pos.getY() <= world.getTopYInclusive() && world.getWorldBorder().contains(pos)) {
         if (state.isAir()) {
            return true;
         } else if (!state.isOf(Blocks.OBSIDIAN) && !state.isOf(Blocks.CRYING_OBSIDIAN) && !state.isOf(Blocks.RESPAWN_ANCHOR) && !state.isOf(Blocks.REINFORCED_DEEPSLATE)) {
            if (direction == Direction.DOWN && pos.getY() == world.getBottomY()) {
               return false;
            } else if (direction == Direction.UP && pos.getY() == world.getTopYInclusive()) {
               return false;
            } else {
               if (!state.isOf(Blocks.PISTON) && !state.isOf(Blocks.STICKY_PISTON)) {
                  if (state.getHardness(world, pos) == -1.0F) {
                     return false;
                  }

                  switch (state.getPistonBehavior()) {
                     case BLOCK:
                        return false;
                     case DESTROY:
                        return canBreak;
                     case PUSH_ONLY:
                        return direction == pistonDir;
                  }
               } else if ((Boolean)state.get(EXTENDED)) {
                  return false;
               }

               return !state.hasBlockEntity();
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean move(World world, BlockPos pos, Direction dir, boolean extend) {
      BlockPos blockPos = pos.offset(dir);
      if (!extend && world.getBlockState(blockPos).isOf(Blocks.PISTON_HEAD)) {
         world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 276);
      }

      PistonHandler pistonHandler = new PistonHandler(world, pos, dir, extend);
      if (!pistonHandler.calculatePush()) {
         return false;
      } else {
         Map map = Maps.newHashMap();
         List list = pistonHandler.getMovedBlocks();
         List list2 = Lists.newArrayList();
         Iterator var10 = list.iterator();

         while(var10.hasNext()) {
            BlockPos blockPos2 = (BlockPos)var10.next();
            BlockState blockState = world.getBlockState(blockPos2);
            list2.add(blockState);
            map.put(blockPos2, blockState);
         }

         List list3 = pistonHandler.getBrokenBlocks();
         BlockState[] blockStates = new BlockState[list.size() + list3.size()];
         Direction direction = extend ? dir : dir.getOpposite();
         int i = 0;

         int j;
         BlockPos blockPos3;
         BlockState blockState2;
         for(j = list3.size() - 1; j >= 0; --j) {
            blockPos3 = (BlockPos)list3.get(j);
            blockState2 = world.getBlockState(blockPos3);
            BlockEntity blockEntity = blockState2.hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
            dropStacks(blockState2, world, blockPos3, blockEntity);
            if (!blockState2.isIn(BlockTags.FIRE) && world.isClient()) {
               world.syncWorldEvent(2001, blockPos3, getRawIdFromState(blockState2));
            }

            world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 18);
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos3, GameEvent.Emitter.of(blockState2));
            blockStates[i++] = blockState2;
         }

         BlockState blockState3;
         for(j = list.size() - 1; j >= 0; --j) {
            blockPos3 = (BlockPos)list.get(j);
            blockState2 = world.getBlockState(blockPos3);
            blockPos3 = blockPos3.offset(direction);
            map.remove(blockPos3);
            blockState3 = (BlockState)Blocks.MOVING_PISTON.getDefaultState().with(FACING, dir);
            world.setBlockState(blockPos3, blockState3, 324);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos3, blockState3, (BlockState)list2.get(j), dir, extend, false));
            blockStates[i++] = blockState2;
         }

         if (extend) {
            PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState blockState4 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir)).with(PistonHeadBlock.TYPE, pistonType);
            blockState2 = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, dir)).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
            map.remove(blockPos);
            world.setBlockState(blockPos, blockState2, 324);
            world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos, blockState2, blockState4, dir, true, true));
         }

         BlockState blockState5 = Blocks.AIR.getDefaultState();
         Iterator var26 = map.keySet().iterator();

         while(var26.hasNext()) {
            BlockPos blockPos4 = (BlockPos)var26.next();
            world.setBlockState(blockPos4, blockState5, 82);
         }

         var26 = map.entrySet().iterator();

         while(var26.hasNext()) {
            Map.Entry entry = (Map.Entry)var26.next();
            BlockPos blockPos5 = (BlockPos)entry.getKey();
            BlockState blockState6 = (BlockState)entry.getValue();
            blockState6.prepare(world, blockPos5, 2);
            blockState5.updateNeighbors(world, blockPos5, 2);
            blockState5.prepare(world, blockPos5, 2);
         }

         WireOrientation wireOrientation = OrientationHelper.getEmissionOrientation(world, pistonHandler.getMotionDirection(), (Direction)null);
         i = 0;

         int k;
         for(k = list3.size() - 1; k >= 0; --k) {
            blockState3 = blockStates[i++];
            BlockPos blockPos6 = (BlockPos)list3.get(k);
            if (world instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)world;
               blockState3.onStateReplaced(serverWorld, blockPos6, false);
            }

            blockState3.prepare(world, blockPos6, 2);
            world.updateNeighborsAlways(blockPos6, blockState3.getBlock(), wireOrientation);
         }

         for(k = list.size() - 1; k >= 0; --k) {
            world.updateNeighborsAlways((BlockPos)list.get(k), blockStates[i++].getBlock(), wireOrientation);
         }

         if (extend) {
            world.updateNeighborsAlways(blockPos, Blocks.PISTON_HEAD, wireOrientation);
         }

         return true;
      }
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FACING, EXTENDED);
   }

   protected boolean hasSidedTransparency(BlockState state) {
      return (Boolean)state.get(EXTENDED);
   }

   protected boolean canPathfindThrough(BlockState state, NavigationType type) {
      return false;
   }

   static {
      EXTENDED = Properties.EXTENDED;
      EXTENDED_SHAPES_BY_DIRECTION = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape(16.0, 4.0, 16.0));
   }
}
