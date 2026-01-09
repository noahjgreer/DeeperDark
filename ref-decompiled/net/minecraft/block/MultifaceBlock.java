package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class MultifaceBlock extends Block implements Waterloggable {
   public static final MapCodec CODEC = createCodec(MultifaceBlock::new);
   public static final BooleanProperty WATERLOGGED;
   private static final Map FACING_PROPERTIES;
   protected static final Direction[] DIRECTIONS;
   private final Function shapeFunction;
   private final boolean hasAllHorizontalDirections;
   private final boolean canMirrorX;
   private final boolean canMirrorZ;

   protected MapCodec getCodec() {
      return CODEC;
   }

   public MultifaceBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState(withAllDirections(this.stateManager));
      this.shapeFunction = this.createShapeFunction();
      this.hasAllHorizontalDirections = Direction.Type.HORIZONTAL.stream().allMatch(this::canHaveDirection);
      this.canMirrorX = Direction.Type.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::canHaveDirection).count() % 2L == 0L;
      this.canMirrorZ = Direction.Type.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::canHaveDirection).count() % 2L == 0L;
   }

   private Function createShapeFunction() {
      Map map = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape(16.0, 0.0, 1.0));
      return this.createShapeFunction((state) -> {
         VoxelShape voxelShape = VoxelShapes.empty();
         Direction[] var3 = DIRECTIONS;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Direction direction = var3[var5];
            if (hasDirection(state, direction)) {
               voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)map.get(direction));
            }
         }

         return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
      }, new Property[]{WATERLOGGED});
   }

   public static Set collectDirections(BlockState state) {
      if (!(state.getBlock() instanceof MultifaceBlock)) {
         return Set.of();
      } else {
         Set set = EnumSet.noneOf(Direction.class);
         Direction[] var2 = Direction.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Direction direction = var2[var4];
            if (hasDirection(state, direction)) {
               set.add(direction);
            }
         }

         return set;
      }
   }

   public static Set flagToDirections(byte flag) {
      Set set = EnumSet.noneOf(Direction.class);
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction = var2[var4];
         if ((flag & (byte)(1 << direction.ordinal())) > 0) {
            set.add(direction);
         }
      }

      return set;
   }

   public static byte directionsToFlag(Collection directions) {
      byte b = 0;

      Direction direction;
      for(Iterator var2 = directions.iterator(); var2.hasNext(); b = (byte)(b | 1 << direction.ordinal())) {
         direction = (Direction)var2.next();
      }

      return b;
   }

   protected boolean canHaveDirection(Direction direction) {
      return true;
   }

   protected void appendProperties(StateManager.Builder builder) {
      Direction[] var2 = DIRECTIONS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction direction = var2[var4];
         if (this.canHaveDirection(direction)) {
            builder.add(getProperty(direction));
         }
      }

      builder.add(WATERLOGGED);
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if ((Boolean)state.get(WATERLOGGED)) {
         tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      if (!hasAnyDirection(state)) {
         return Blocks.AIR.getDefaultState();
      } else {
         return hasDirection(state, direction) && !canGrowOn(world, direction, neighborPos, neighborState) ? disableDirection(state, getProperty(direction)) : state;
      }
   }

   protected FluidState getFluidState(BlockState state) {
      return (Boolean)state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)this.shapeFunction.apply(state);
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      boolean bl = false;
      Direction[] var5 = DIRECTIONS;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction direction = var5[var7];
         if (hasDirection(state, direction)) {
            if (!canGrowOn(world, pos, direction)) {
               return false;
            }

            bl = true;
         }
      }

      return bl;
   }

   protected boolean canReplace(BlockState state, ItemPlacementContext context) {
      return !context.getStack().isOf(this.asItem()) || isNotFullBlock(state);
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      World world = ctx.getWorld();
      BlockPos blockPos = ctx.getBlockPos();
      BlockState blockState = world.getBlockState(blockPos);
      return (BlockState)Arrays.stream(ctx.getPlacementDirections()).map((direction) -> {
         return this.withDirection(blockState, world, blockPos, direction);
      }).filter(Objects::nonNull).findFirst().orElse((Object)null);
   }

   public boolean canGrowWithDirection(BlockView world, BlockState state, BlockPos pos, Direction direction) {
      if (this.canHaveDirection(direction) && (!state.isOf(this) || !hasDirection(state, direction))) {
         BlockPos blockPos = pos.offset(direction);
         return canGrowOn(world, direction, blockPos, world.getBlockState(blockPos));
      } else {
         return false;
      }
   }

   @Nullable
   public BlockState withDirection(BlockState state, BlockView world, BlockPos pos, Direction direction) {
      if (!this.canGrowWithDirection(world, state, pos, direction)) {
         return null;
      } else {
         BlockState blockState;
         if (state.isOf(this)) {
            blockState = state;
         } else if (state.getFluidState().isEqualAndStill(Fluids.WATER)) {
            blockState = (BlockState)this.getDefaultState().with(Properties.WATERLOGGED, true);
         } else {
            blockState = this.getDefaultState();
         }

         return (BlockState)blockState.with(getProperty(direction), true);
      }
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      if (!this.hasAllHorizontalDirections) {
         return state;
      } else {
         Objects.requireNonNull(rotation);
         return this.mirror(state, rotation::rotate);
      }
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      if (mirror == BlockMirror.FRONT_BACK && !this.canMirrorX) {
         return state;
      } else if (mirror == BlockMirror.LEFT_RIGHT && !this.canMirrorZ) {
         return state;
      } else {
         Objects.requireNonNull(mirror);
         return this.mirror(state, mirror::apply);
      }
   }

   private BlockState mirror(BlockState state, Function mirror) {
      BlockState blockState = state;
      Direction[] var4 = DIRECTIONS;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction direction = var4[var6];
         if (this.canHaveDirection(direction)) {
            blockState = (BlockState)blockState.with(getProperty((Direction)mirror.apply(direction)), (Boolean)state.get(getProperty(direction)));
         }
      }

      return blockState;
   }

   public static boolean hasDirection(BlockState state, Direction direction) {
      BooleanProperty booleanProperty = getProperty(direction);
      return (Boolean)state.get(booleanProperty, false);
   }

   public static boolean canGrowOn(BlockView world, BlockPos pos, Direction direction) {
      BlockPos blockPos = pos.offset(direction);
      BlockState blockState = world.getBlockState(blockPos);
      return canGrowOn(world, direction, blockPos, blockState);
   }

   public static boolean canGrowOn(BlockView world, Direction direction, BlockPos pos, BlockState state) {
      return Block.isFaceFullSquare(state.getSidesShape(world, pos), direction.getOpposite()) || Block.isFaceFullSquare(state.getCollisionShape(world, pos), direction.getOpposite());
   }

   private static BlockState disableDirection(BlockState state, BooleanProperty direction) {
      BlockState blockState = (BlockState)state.with(direction, false);
      return hasAnyDirection(blockState) ? blockState : Blocks.AIR.getDefaultState();
   }

   public static BooleanProperty getProperty(Direction direction) {
      return (BooleanProperty)FACING_PROPERTIES.get(direction);
   }

   private static BlockState withAllDirections(StateManager stateManager) {
      BlockState blockState = (BlockState)((BlockState)stateManager.getDefaultState()).with(WATERLOGGED, false);

      BooleanProperty booleanProperty;
      for(Iterator var2 = FACING_PROPERTIES.values().iterator(); var2.hasNext(); blockState = (BlockState)blockState.withIfExists(booleanProperty, false)) {
         booleanProperty = (BooleanProperty)var2.next();
      }

      return blockState;
   }

   protected static boolean hasAnyDirection(BlockState state) {
      Direction[] var1 = DIRECTIONS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Direction direction = var1[var3];
         if (hasDirection(state, direction)) {
            return true;
         }
      }

      return false;
   }

   private static boolean isNotFullBlock(BlockState state) {
      Direction[] var1 = DIRECTIONS;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Direction direction = var1[var3];
         if (!hasDirection(state, direction)) {
            return true;
         }
      }

      return false;
   }

   static {
      WATERLOGGED = Properties.WATERLOGGED;
      FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
      DIRECTIONS = Direction.values();
   }
}
