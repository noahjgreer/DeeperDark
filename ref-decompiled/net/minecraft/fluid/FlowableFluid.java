package net.minecraft.fluid;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.IceBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class FlowableFluid extends Fluid {
   public static final BooleanProperty FALLING;
   public static final IntProperty LEVEL;
   private static final int field_31726 = 200;
   private static final ThreadLocal field_15901;
   private final Map shapeCache = Maps.newIdentityHashMap();

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(FALLING);
   }

   public Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
      double d = 0.0;
      double e = 0.0;
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Iterator var9 = Direction.Type.HORIZONTAL.iterator();

      while(var9.hasNext()) {
         Direction direction = (Direction)var9.next();
         mutable.set(pos, (Direction)direction);
         FluidState fluidState = world.getFluidState(mutable);
         if (this.isEmptyOrThis(fluidState)) {
            float f = fluidState.getHeight();
            float g = 0.0F;
            if (f == 0.0F) {
               if (!world.getBlockState(mutable).blocksMovement()) {
                  BlockPos blockPos = mutable.down();
                  FluidState fluidState2 = world.getFluidState(blockPos);
                  if (this.isEmptyOrThis(fluidState2)) {
                     f = fluidState2.getHeight();
                     if (f > 0.0F) {
                        g = state.getHeight() - (f - 0.8888889F);
                     }
                  }
               }
            } else if (f > 0.0F) {
               g = state.getHeight() - f;
            }

            if (g != 0.0F) {
               d += (double)((float)direction.getOffsetX() * g);
               e += (double)((float)direction.getOffsetZ() * g);
            }
         }
      }

      Vec3d vec3d = new Vec3d(d, 0.0, e);
      if ((Boolean)state.get(FALLING)) {
         Iterator var17 = Direction.Type.HORIZONTAL.iterator();

         Direction direction2;
         do {
            if (!var17.hasNext()) {
               return vec3d.normalize();
            }

            direction2 = (Direction)var17.next();
            mutable.set(pos, (Direction)direction2);
         } while(!this.isFlowBlocked(world, mutable, direction2) && !this.isFlowBlocked(world, mutable.up(), direction2));

         vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
      }

      return vec3d.normalize();
   }

   private boolean isEmptyOrThis(FluidState state) {
      return state.isEmpty() || state.getFluid().matchesType(this);
   }

   protected boolean isFlowBlocked(BlockView world, BlockPos pos, Direction direction) {
      BlockState blockState = world.getBlockState(pos);
      FluidState fluidState = world.getFluidState(pos);
      if (fluidState.getFluid().matchesType(this)) {
         return false;
      } else if (direction == Direction.UP) {
         return true;
      } else {
         return blockState.getBlock() instanceof IceBlock ? false : blockState.isSideSolidFullSquare(world, pos, direction);
      }
   }

   protected void tryFlow(ServerWorld world, BlockPos fluidPos, BlockState blockState, FluidState fluidState) {
      if (!fluidState.isEmpty()) {
         BlockPos blockPos = fluidPos.down();
         BlockState blockState2 = world.getBlockState(blockPos);
         FluidState fluidState2 = blockState2.getFluidState();
         if (this.canFlowThrough(world, fluidPos, blockState, Direction.DOWN, blockPos, blockState2, fluidState2)) {
            FluidState fluidState3 = this.getUpdatedState(world, blockPos, blockState2);
            Fluid fluid = fluidState3.getFluid();
            if (fluidState2.canBeReplacedWith(world, blockPos, fluid, Direction.DOWN) && canFillWithFluid(world, blockPos, blockState2, fluid)) {
               this.flow(world, blockPos, blockState2, Direction.DOWN, fluidState3);
               if (this.countNeighboringSources(world, fluidPos) >= 3) {
                  this.flowToSides(world, fluidPos, fluidState, blockState);
               }

               return;
            }
         }

         if (fluidState.isStill() || !this.canFlowDownTo(world, fluidPos, blockState, blockPos, blockState2)) {
            this.flowToSides(world, fluidPos, fluidState, blockState);
         }

      }
   }

   private void flowToSides(ServerWorld world, BlockPos pos, FluidState fluidState, BlockState blockState) {
      int i = fluidState.getLevel() - this.getLevelDecreasePerBlock(world);
      if ((Boolean)fluidState.get(FALLING)) {
         i = 7;
      }

      if (i > 0) {
         Map map = this.getSpread(world, pos, blockState);
         Iterator var7 = map.entrySet().iterator();

         while(var7.hasNext()) {
            Map.Entry entry = (Map.Entry)var7.next();
            Direction direction = (Direction)entry.getKey();
            FluidState fluidState2 = (FluidState)entry.getValue();
            BlockPos blockPos = pos.offset(direction);
            this.flow(world, blockPos, world.getBlockState(blockPos), direction, fluidState2);
         }

      }
   }

   protected FluidState getUpdatedState(ServerWorld world, BlockPos pos, BlockState state) {
      int i = 0;
      int j = 0;
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Iterator var7 = Direction.Type.HORIZONTAL.iterator();

      while(var7.hasNext()) {
         Direction direction = (Direction)var7.next();
         BlockPos blockPos = mutable.set(pos, (Direction)direction);
         BlockState blockState = world.getBlockState(blockPos);
         FluidState fluidState = blockState.getFluidState();
         if (fluidState.getFluid().matchesType(this) && receivesFlow(direction, world, pos, state, blockPos, blockState)) {
            if (fluidState.isStill()) {
               ++j;
            }

            i = Math.max(i, fluidState.getLevel());
         }
      }

      if (j >= 2 && this.isInfinite(world)) {
         BlockState blockState2 = world.getBlockState(mutable.set(pos, (Direction)Direction.DOWN));
         FluidState fluidState2 = blockState2.getFluidState();
         if (blockState2.isSolid() || this.isMatchingAndStill(fluidState2)) {
            return this.getStill(false);
         }
      }

      BlockPos blockPos2 = mutable.set(pos, (Direction)Direction.UP);
      BlockState blockState3 = world.getBlockState(blockPos2);
      FluidState fluidState3 = blockState3.getFluidState();
      if (!fluidState3.isEmpty() && fluidState3.getFluid().matchesType(this) && receivesFlow(Direction.UP, world, pos, state, blockPos2, blockState3)) {
         return this.getFlowing(8, true);
      } else {
         int k = i - this.getLevelDecreasePerBlock(world);
         return k <= 0 ? Fluids.EMPTY.getDefaultState() : this.getFlowing(k, false);
      }
   }

   private static boolean receivesFlow(Direction face, BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
      VoxelShape voxelShape = fromState.getCollisionShape(world, fromPos);
      if (voxelShape == VoxelShapes.fullCube()) {
         return false;
      } else {
         VoxelShape voxelShape2 = state.getCollisionShape(world, pos);
         if (voxelShape2 == VoxelShapes.fullCube()) {
            return false;
         } else if (voxelShape2 == VoxelShapes.empty() && voxelShape == VoxelShapes.empty()) {
            return true;
         } else {
            Object2ByteLinkedOpenHashMap object2ByteLinkedOpenHashMap;
            if (!state.getBlock().hasDynamicBounds() && !fromState.getBlock().hasDynamicBounds()) {
               object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap)field_15901.get();
            } else {
               object2ByteLinkedOpenHashMap = null;
            }

            NeighborGroup neighborGroup;
            if (object2ByteLinkedOpenHashMap != null) {
               neighborGroup = new NeighborGroup(state, fromState, face);
               byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
               if (b != 127) {
                  return b != 0;
               }
            } else {
               neighborGroup = null;
            }

            boolean bl = !VoxelShapes.adjacentSidesCoverSquare(voxelShape2, voxelShape, face);
            if (object2ByteLinkedOpenHashMap != null) {
               if (object2ByteLinkedOpenHashMap.size() == 200) {
                  object2ByteLinkedOpenHashMap.removeLastByte();
               }

               object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
            }

            return bl;
         }
      }
   }

   public abstract Fluid getFlowing();

   public FluidState getFlowing(int level, boolean falling) {
      return (FluidState)((FluidState)this.getFlowing().getDefaultState().with(LEVEL, level)).with(FALLING, falling);
   }

   public abstract Fluid getStill();

   public FluidState getStill(boolean falling) {
      return (FluidState)this.getStill().getDefaultState().with(FALLING, falling);
   }

   protected abstract boolean isInfinite(ServerWorld world);

   protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
      Block var7 = state.getBlock();
      if (var7 instanceof FluidFillable fluidFillable) {
         fluidFillable.tryFillWithFluid(world, pos, state, fluidState);
      } else {
         if (!state.isAir()) {
            this.beforeBreakingBlock(world, pos, state);
         }

         world.setBlockState(pos, fluidState.getBlockState(), 3);
      }

   }

   protected abstract void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state);

   protected int getMinFlowDownDistance(WorldView world, BlockPos pos, int i, Direction direction, BlockState state, SpreadCache spreadCache) {
      int j = 1000;
      Iterator var8 = Direction.Type.HORIZONTAL.iterator();

      while(var8.hasNext()) {
         Direction direction2 = (Direction)var8.next();
         if (direction2 != direction) {
            BlockPos blockPos = pos.offset(direction2);
            BlockState blockState = spreadCache.getBlockState(blockPos);
            FluidState fluidState = blockState.getFluidState();
            if (this.canFlowThrough(world, this.getFlowing(), pos, state, direction2, blockPos, blockState, fluidState)) {
               if (spreadCache.canFlowDownTo(blockPos)) {
                  return i;
               }

               if (i < this.getMaxFlowDistance(world)) {
                  int k = this.getMinFlowDownDistance(world, blockPos, i + 1, direction2.getOpposite(), blockState, spreadCache);
                  if (k < j) {
                     j = k;
                  }
               }
            }
         }
      }

      return j;
   }

   boolean canFlowDownTo(BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
      if (!receivesFlow(Direction.DOWN, world, pos, state, fromPos, fromState)) {
         return false;
      } else {
         return fromState.getFluidState().getFluid().matchesType(this) ? true : canFill(world, fromPos, fromState, this.getFlowing());
      }
   }

   private boolean canFlowThrough(BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState) {
      return this.canFlowThrough(world, pos, state, face, fromPos, fromState, fluidState) && canFillWithFluid(world, fromPos, fromState, fluid);
   }

   private boolean canFlowThrough(BlockView world, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState) {
      return !this.isMatchingAndStill(fluidState) && canFill(fromState) && receivesFlow(face, world, pos, state, fromPos, fromState);
   }

   private boolean isMatchingAndStill(FluidState state) {
      return state.getFluid().matchesType(this) && state.isStill();
   }

   protected abstract int getMaxFlowDistance(WorldView world);

   private int countNeighboringSources(WorldView world, BlockPos pos) {
      int i = 0;
      Iterator var4 = Direction.Type.HORIZONTAL.iterator();

      while(var4.hasNext()) {
         Direction direction = (Direction)var4.next();
         BlockPos blockPos = pos.offset(direction);
         FluidState fluidState = world.getFluidState(blockPos);
         if (this.isMatchingAndStill(fluidState)) {
            ++i;
         }
      }

      return i;
   }

   protected Map getSpread(ServerWorld world, BlockPos pos, BlockState state) {
      int i = 1000;
      Map map = Maps.newEnumMap(Direction.class);
      SpreadCache spreadCache = null;
      Iterator var7 = Direction.Type.HORIZONTAL.iterator();

      while(var7.hasNext()) {
         Direction direction = (Direction)var7.next();
         BlockPos blockPos = pos.offset(direction);
         BlockState blockState = world.getBlockState(blockPos);
         FluidState fluidState = blockState.getFluidState();
         if (this.canFlowThrough(world, pos, state, direction, blockPos, blockState, fluidState)) {
            FluidState fluidState2 = this.getUpdatedState(world, blockPos, blockState);
            if (canFillWithFluid(world, blockPos, blockState, fluidState2.getFluid())) {
               if (spreadCache == null) {
                  spreadCache = new SpreadCache(world, pos);
               }

               int j;
               if (spreadCache.canFlowDownTo(blockPos)) {
                  j = 0;
               } else {
                  j = this.getMinFlowDownDistance(world, blockPos, 1, direction.getOpposite(), blockState, spreadCache);
               }

               if (j < i) {
                  map.clear();
               }

               if (j <= i) {
                  if (fluidState.canBeReplacedWith(world, blockPos, fluidState2.getFluid(), direction)) {
                     map.put(direction, fluidState2);
                  }

                  i = j;
               }
            }
         }
      }

      return map;
   }

   private static boolean canFill(BlockState state) {
      Block block = state.getBlock();
      if (block instanceof FluidFillable) {
         return true;
      } else if (state.blocksMovement()) {
         return false;
      } else {
         return !(block instanceof DoorBlock) && !state.isIn(BlockTags.SIGNS) && !state.isOf(Blocks.LADDER) && !state.isOf(Blocks.SUGAR_CANE) && !state.isOf(Blocks.BUBBLE_COLUMN) && !state.isOf(Blocks.NETHER_PORTAL) && !state.isOf(Blocks.END_PORTAL) && !state.isOf(Blocks.END_GATEWAY) && !state.isOf(Blocks.STRUCTURE_VOID);
      }
   }

   private static boolean canFill(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
      return canFill(state) && canFillWithFluid(world, pos, state, fluid);
   }

   private static boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
      Block block = state.getBlock();
      if (block instanceof FluidFillable fluidFillable) {
         return fluidFillable.canFillWithFluid((LivingEntity)null, world, pos, state, fluid);
      } else {
         return true;
      }
   }

   protected abstract int getLevelDecreasePerBlock(WorldView world);

   protected int getNextTickDelay(World world, BlockPos pos, FluidState oldState, FluidState newState) {
      return this.getTickRate(world);
   }

   public void onScheduledTick(ServerWorld world, BlockPos pos, BlockState blockState, FluidState fluidState) {
      if (!fluidState.isStill()) {
         FluidState fluidState2 = this.getUpdatedState(world, pos, world.getBlockState(pos));
         int i = this.getNextTickDelay(world, pos, fluidState, fluidState2);
         if (fluidState2.isEmpty()) {
            fluidState = fluidState2;
            blockState = Blocks.AIR.getDefaultState();
            world.setBlockState(pos, blockState, 3);
         } else if (fluidState2 != fluidState) {
            fluidState = fluidState2;
            blockState = fluidState2.getBlockState();
            world.setBlockState(pos, blockState, 3);
            world.scheduleFluidTick(pos, fluidState2.getFluid(), i);
         }
      }

      this.tryFlow(world, pos, blockState, fluidState);
   }

   protected static int getBlockStateLevel(FluidState state) {
      return state.isStill() ? 0 : 8 - Math.min(state.getLevel(), 8) + ((Boolean)state.get(FALLING) ? 8 : 0);
   }

   private static boolean isFluidAboveEqual(FluidState state, BlockView world, BlockPos pos) {
      return state.getFluid().matchesType(world.getFluidState(pos.up()).getFluid());
   }

   public float getHeight(FluidState state, BlockView world, BlockPos pos) {
      return isFluidAboveEqual(state, world, pos) ? 1.0F : state.getHeight();
   }

   public float getHeight(FluidState state) {
      return (float)state.getLevel() / 9.0F;
   }

   public abstract int getLevel(FluidState state);

   public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
      return state.getLevel() == 9 && isFluidAboveEqual(state, world, pos) ? VoxelShapes.fullCube() : (VoxelShape)this.shapeCache.computeIfAbsent(state, (state2) -> {
         return VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)state2.getHeight(world, pos), 1.0);
      });
   }

   static {
      FALLING = Properties.FALLING;
      LEVEL = Properties.LEVEL_1_8;
      field_15901 = ThreadLocal.withInitial(() -> {
         Object2ByteLinkedOpenHashMap object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap(200) {
            protected void rehash(int i) {
            }
         };
         object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
         return object2ByteLinkedOpenHashMap;
      });
   }

   static record NeighborGroup(BlockState self, BlockState other, Direction facing) {
      NeighborGroup(BlockState self, BlockState other, Direction facing) {
         this.self = self;
         this.other = other;
         this.facing = facing;
      }

      public boolean equals(Object o) {
         boolean var10000;
         if (o instanceof NeighborGroup neighborGroup) {
            if (this.self == neighborGroup.self && this.other == neighborGroup.other && this.facing == neighborGroup.facing) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }

      public int hashCode() {
         int i = System.identityHashCode(this.self);
         i = 31 * i + System.identityHashCode(this.other);
         i = 31 * i + this.facing.hashCode();
         return i;
      }

      public BlockState self() {
         return this.self;
      }

      public BlockState other() {
         return this.other;
      }

      public Direction facing() {
         return this.facing;
      }
   }

   protected class SpreadCache {
      private final BlockView world;
      private final BlockPos startPos;
      private final Short2ObjectMap stateCache = new Short2ObjectOpenHashMap();
      private final Short2BooleanMap flowDownCache = new Short2BooleanOpenHashMap();

      SpreadCache(final BlockView world, final BlockPos startPos) {
         this.world = world;
         this.startPos = startPos;
      }

      public BlockState getBlockState(BlockPos pos) {
         return this.getBlockState(pos, this.pack(pos));
      }

      private BlockState getBlockState(BlockPos pos, short packed) {
         return (BlockState)this.stateCache.computeIfAbsent(packed, (packedPos) -> {
            return this.world.getBlockState(pos);
         });
      }

      public boolean canFlowDownTo(BlockPos pos) {
         return this.flowDownCache.computeIfAbsent(this.pack(pos), (packed) -> {
            BlockState blockState = this.getBlockState(pos, packed);
            BlockPos blockPos2 = pos.down();
            BlockState blockState2 = this.world.getBlockState(blockPos2);
            return FlowableFluid.this.canFlowDownTo(this.world, pos, blockState, blockPos2, blockState2);
         });
      }

      private short pack(BlockPos pos) {
         int i = pos.getX() - this.startPos.getX();
         int j = pos.getZ() - this.startPos.getZ();
         return (short)((i + 128 & 255) << 8 | j + 128 & 255);
      }
   }
}
