package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class PitcherCropBlock extends TallPlantBlock implements Fertilizable {
   public static final MapCodec CODEC = createCodec(PitcherCropBlock::new);
   public static final int field_43240 = 4;
   public static final IntProperty AGE;
   public static final EnumProperty HALF;
   private static final int field_43241 = 3;
   private static final int field_43391 = 1;
   private static final VoxelShape AGE_0_SHAPE;
   private static final VoxelShape LOWER_COLLISION_SHAPE;
   private final Function shapeFunction = this.createShapeFunction();

   public MapCodec getCodec() {
      return CODEC;
   }

   public PitcherCropBlock(AbstractBlock.Settings settings) {
      super(settings);
   }

   private Function createShapeFunction() {
      int[] is = new int[]{0, 9, 11, 22, 26};
      return this.createShapeFunction((state) -> {
         int i = ((Integer)state.get(AGE) == 0 ? 4 : 6) + is[(Integer)state.get(AGE)];
         int j = (Integer)state.get(AGE) == 0 ? 6 : 10;
         VoxelShape var10000;
         switch ((DoubleBlockHalf)state.get(HALF)) {
            case LOWER:
               var10000 = Block.createColumnShape((double)j, -1.0, (double)Math.min(16, -1 + i));
               break;
            case UPPER:
               var10000 = Block.createColumnShape((double)j, 0.0, (double)Math.max(0, -1 + i - 16));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return this.getDefaultState();
   }

   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)this.shapeFunction.apply(state);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      if (state.get(HALF) == DoubleBlockHalf.LOWER) {
         return (Integer)state.get(AGE) == 0 ? AGE_0_SHAPE : LOWER_COLLISION_SHAPE;
      } else {
         return VoxelShapes.empty();
      }
   }

   public BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (isDoubleTallAtAge((Integer)state.get(AGE))) {
         return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
      } else {
         return state.canPlaceAt(world, pos) ? state : Blocks.AIR.getDefaultState();
      }
   }

   public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return isLowerHalf(state) && !canPlaceAt(world, pos) ? false : super.canPlaceAt(state, world, pos);
   }

   protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
      return floor.isOf(Blocks.FARMLAND);
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(AGE);
      super.appendProperties(builder);
   }

   public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
      if (world instanceof ServerWorld serverWorld) {
         if (entity instanceof RavagerEntity && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            serverWorld.breakBlock(pos, true, entity);
         }
      }

   }

   public boolean canReplace(BlockState state, ItemPlacementContext context) {
      return false;
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
   }

   public boolean hasRandomTicks(BlockState state) {
      return state.get(HALF) == DoubleBlockHalf.LOWER && !this.isFullyGrown(state);
   }

   public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
      float f = CropBlock.getAvailableMoisture(this, world, pos);
      boolean bl = random.nextInt((int)(25.0F / f) + 1) == 0;
      if (bl) {
         this.tryGrow(world, state, pos, 1);
      }

   }

   private void tryGrow(ServerWorld world, BlockState state, BlockPos pos, int amount) {
      int i = Math.min((Integer)state.get(AGE) + amount, 4);
      if (this.canGrow(world, pos, state, i)) {
         BlockState blockState = (BlockState)state.with(AGE, i);
         world.setBlockState(pos, blockState, 2);
         if (isDoubleTallAtAge(i)) {
            world.setBlockState(pos.up(), (BlockState)blockState.with(HALF, DoubleBlockHalf.UPPER), 3);
         }

      }
   }

   private static boolean canGrowAt(WorldView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      return blockState.isAir() || blockState.isOf(Blocks.PITCHER_CROP);
   }

   private static boolean canPlaceAt(WorldView world, BlockPos pos) {
      return CropBlock.hasEnoughLightAt(world, pos);
   }

   private static boolean isLowerHalf(BlockState state) {
      return state.isOf(Blocks.PITCHER_CROP) && state.get(HALF) == DoubleBlockHalf.LOWER;
   }

   private static boolean isDoubleTallAtAge(int age) {
      return age >= 3;
   }

   private boolean canGrow(WorldView world, BlockPos pos, BlockState state, int age) {
      return !this.isFullyGrown(state) && canPlaceAt(world, pos) && (!isDoubleTallAtAge(age) || canGrowAt(world, pos.up()));
   }

   private boolean isFullyGrown(BlockState state) {
      return (Integer)state.get(AGE) >= 4;
   }

   @Nullable
   private LowerHalfContext getLowerHalfContext(WorldView world, BlockPos pos, BlockState state) {
      if (isLowerHalf(state)) {
         return new LowerHalfContext(pos, state);
      } else {
         BlockPos blockPos = pos.down();
         BlockState blockState = world.getBlockState(blockPos);
         return isLowerHalf(blockState) ? new LowerHalfContext(blockPos, blockState) : null;
      }
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      LowerHalfContext lowerHalfContext = this.getLowerHalfContext(world, pos, state);
      return lowerHalfContext == null ? false : this.canGrow(world, lowerHalfContext.pos, lowerHalfContext.state, (Integer)lowerHalfContext.state.get(AGE) + 1);
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      LowerHalfContext lowerHalfContext = this.getLowerHalfContext(world, pos, state);
      if (lowerHalfContext != null) {
         this.tryGrow(world, lowerHalfContext.state, lowerHalfContext.pos, 1);
      }
   }

   static {
      AGE = Properties.AGE_4;
      HALF = TallPlantBlock.HALF;
      AGE_0_SHAPE = Block.createColumnShape(6.0, -1.0, 3.0);
      LOWER_COLLISION_SHAPE = Block.createColumnShape(10.0, -1.0, 5.0);
   }

   static record LowerHalfContext(BlockPos pos, BlockState state) {
      final BlockPos pos;
      final BlockState state;

      LowerHalfContext(BlockPos blockPos, BlockState blockState) {
         this.pos = blockPos;
         this.state = blockState;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public BlockState state() {
         return this.state;
      }
   }
}
