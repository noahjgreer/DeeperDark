package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
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
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;

public class PaleMossCarpetBlock extends Block implements Fertilizable {
   public static final MapCodec CODEC = createCodec(PaleMossCarpetBlock::new);
   public static final BooleanProperty BOTTOM;
   public static final EnumProperty NORTH;
   public static final EnumProperty EAST;
   public static final EnumProperty SOUTH;
   public static final EnumProperty WEST;
   public static final Map WALL_SHAPE_PROPERTIES_BY_DIRECTION;
   private final Function shapeFunction;

   public MapCodec getCodec() {
      return CODEC;
   }

   public PaleMossCarpetBlock(AbstractBlock.Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(BOTTOM, true)).with(NORTH, WallShape.NONE)).with(EAST, WallShape.NONE)).with(SOUTH, WallShape.NONE)).with(WEST, WallShape.NONE));
      this.shapeFunction = this.createShapeFunction();
   }

   protected VoxelShape getCullingShape(BlockState state) {
      return VoxelShapes.empty();
   }

   public Function createShapeFunction() {
      Map map = VoxelShapes.createHorizontalFacingShapeMap(Block.createCuboidZShape(16.0, 0.0, 10.0, 0.0, 1.0));
      Map map2 = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape(16.0, 0.0, 1.0));
      return this.createShapeFunction((state) -> {
         VoxelShape voxelShape = (Boolean)state.get(BOTTOM) ? (VoxelShape)map2.get(Direction.DOWN) : VoxelShapes.empty();
         Iterator var4 = WALL_SHAPE_PROPERTIES_BY_DIRECTION.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            switch ((WallShape)state.get((Property)entry.getValue())) {
               case NONE:
               default:
                  break;
               case LOW:
                  voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)map.get(entry.getKey()));
                  break;
               case TALL:
                  voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)map2.get(entry.getKey()));
            }
         }

         return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
      });
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)this.shapeFunction.apply(state);
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (Boolean)state.get(BOTTOM) ? (VoxelShape)this.shapeFunction.apply(this.getDefaultState()) : VoxelShapes.empty();
   }

   protected boolean isTransparent(BlockState state) {
      return true;
   }

   protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos.down());
      if ((Boolean)state.get(BOTTOM)) {
         return !blockState.isAir();
      } else {
         return blockState.isOf(this) && (Boolean)blockState.get(BOTTOM);
      }
   }

   private static boolean hasAnyShape(BlockState state) {
      if ((Boolean)state.get(BOTTOM)) {
         return true;
      } else {
         Iterator var1 = WALL_SHAPE_PROPERTIES_BY_DIRECTION.values().iterator();

         EnumProperty enumProperty;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            enumProperty = (EnumProperty)var1.next();
         } while(state.get(enumProperty) == WallShape.NONE);

         return true;
      }
   }

   private static boolean canGrowOnFace(BlockView world, BlockPos pos, Direction direction) {
      return direction == Direction.UP ? false : MultifaceBlock.canGrowOn(world, pos, direction);
   }

   private static BlockState updateState(BlockState state, BlockView world, BlockPos pos, boolean bl) {
      BlockState blockState = null;
      BlockState blockState2 = null;
      bl |= (Boolean)state.get(BOTTOM);

      EnumProperty enumProperty;
      WallShape wallShape;
      for(Iterator var6 = Direction.Type.HORIZONTAL.iterator(); var6.hasNext(); state = (BlockState)state.with(enumProperty, wallShape)) {
         Direction direction = (Direction)var6.next();
         enumProperty = getWallShape(direction);
         wallShape = canGrowOnFace(world, pos, direction) ? (bl ? WallShape.LOW : (WallShape)state.get(enumProperty)) : WallShape.NONE;
         if (wallShape == WallShape.LOW) {
            if (blockState == null) {
               blockState = world.getBlockState(pos.up());
            }

            if (blockState.isOf(Blocks.PALE_MOSS_CARPET) && blockState.get(enumProperty) != WallShape.NONE && !(Boolean)blockState.get(BOTTOM)) {
               wallShape = WallShape.TALL;
            }

            if (!(Boolean)state.get(BOTTOM)) {
               if (blockState2 == null) {
                  blockState2 = world.getBlockState(pos.down());
               }

               if (blockState2.isOf(Blocks.PALE_MOSS_CARPET) && blockState2.get(enumProperty) == WallShape.NONE) {
                  wallShape = WallShape.NONE;
               }
            }
         }
      }

      return state;
   }

   @Nullable
   public BlockState getPlacementState(ItemPlacementContext ctx) {
      return updateState(this.getDefaultState(), ctx.getWorld(), ctx.getBlockPos(), true);
   }

   public static void placeAt(WorldAccess world, BlockPos pos, Random random, int flags) {
      BlockState blockState = Blocks.PALE_MOSS_CARPET.getDefaultState();
      BlockState blockState2 = updateState(blockState, world, pos, true);
      world.setBlockState(pos, blockState2, flags);
      Objects.requireNonNull(random);
      BlockState blockState3 = createUpperState(world, pos, random::nextBoolean);
      if (!blockState3.isAir()) {
         world.setBlockState(pos.up(), blockState3, flags);
         BlockState blockState4 = updateState(blockState2, world, pos, true);
         world.setBlockState(pos, blockState4, flags);
      }

   }

   public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
      if (!world.isClient) {
         Random random = world.getRandom();
         Objects.requireNonNull(random);
         BlockState blockState = createUpperState(world, pos, random::nextBoolean);
         if (!blockState.isAir()) {
            world.setBlockState(pos.up(), blockState, 3);
         }

      }
   }

   private static BlockState createUpperState(BlockView world, BlockPos pos, BooleanSupplier booleanSupplier) {
      BlockPos blockPos = pos.up();
      BlockState blockState = world.getBlockState(blockPos);
      boolean bl = blockState.isOf(Blocks.PALE_MOSS_CARPET);
      if ((!bl || !(Boolean)blockState.get(BOTTOM)) && (bl || blockState.isReplaceable())) {
         BlockState blockState2 = (BlockState)Blocks.PALE_MOSS_CARPET.getDefaultState().with(BOTTOM, false);
         BlockState blockState3 = updateState(blockState2, world, pos.up(), true);
         Iterator var8 = Direction.Type.HORIZONTAL.iterator();

         while(var8.hasNext()) {
            Direction direction = (Direction)var8.next();
            EnumProperty enumProperty = getWallShape(direction);
            if (blockState3.get(enumProperty) != WallShape.NONE && !booleanSupplier.getAsBoolean()) {
               blockState3 = (BlockState)blockState3.with(enumProperty, WallShape.NONE);
            }
         }

         if (hasAnyShape(blockState3) && blockState3 != blockState) {
            return blockState3;
         } else {
            return Blocks.AIR.getDefaultState();
         }
      } else {
         return Blocks.AIR.getDefaultState();
      }
   }

   protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
      if (!state.canPlaceAt(world, pos)) {
         return Blocks.AIR.getDefaultState();
      } else {
         BlockState blockState = updateState(state, world, pos, false);
         return !hasAnyShape(blockState) ? Blocks.AIR.getDefaultState() : blockState;
      }
   }

   protected void appendProperties(StateManager.Builder builder) {
      builder.add(BOTTOM, NORTH, EAST, SOUTH, WEST);
   }

   protected BlockState rotate(BlockState state, BlockRotation rotation) {
      BlockState var10000;
      switch (rotation) {
         case CLOCKWISE_180:
            var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, (WallShape)state.get(SOUTH))).with(EAST, (WallShape)state.get(WEST))).with(SOUTH, (WallShape)state.get(NORTH))).with(WEST, (WallShape)state.get(EAST));
            break;
         case COUNTERCLOCKWISE_90:
            var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, (WallShape)state.get(EAST))).with(EAST, (WallShape)state.get(SOUTH))).with(SOUTH, (WallShape)state.get(WEST))).with(WEST, (WallShape)state.get(NORTH));
            break;
         case CLOCKWISE_90:
            var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.with(NORTH, (WallShape)state.get(WEST))).with(EAST, (WallShape)state.get(NORTH))).with(SOUTH, (WallShape)state.get(EAST))).with(WEST, (WallShape)state.get(SOUTH));
            break;
         default:
            var10000 = state;
      }

      return var10000;
   }

   protected BlockState mirror(BlockState state, BlockMirror mirror) {
      BlockState var10000;
      switch (mirror) {
         case LEFT_RIGHT:
            var10000 = (BlockState)((BlockState)state.with(NORTH, (WallShape)state.get(SOUTH))).with(SOUTH, (WallShape)state.get(NORTH));
            break;
         case FRONT_BACK:
            var10000 = (BlockState)((BlockState)state.with(EAST, (WallShape)state.get(WEST))).with(WEST, (WallShape)state.get(EAST));
            break;
         default:
            var10000 = super.mirror(state, mirror);
      }

      return var10000;
   }

   @Nullable
   public static EnumProperty getWallShape(Direction face) {
      return (EnumProperty)WALL_SHAPE_PROPERTIES_BY_DIRECTION.get(face);
   }

   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
      return (Boolean)state.get(BOTTOM) && !createUpperState(world, pos, () -> {
         return true;
      }).isAir();
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      BlockState blockState = createUpperState(world, pos, () -> {
         return true;
      });
      if (!blockState.isAir()) {
         world.setBlockState(pos.up(), blockState, 3);
      }

   }

   static {
      BOTTOM = Properties.BOTTOM;
      NORTH = Properties.NORTH_WALL_SHAPE;
      EAST = Properties.EAST_WALL_SHAPE;
      SOUTH = Properties.SOUTH_WALL_SHAPE;
      WEST = Properties.WEST_WALL_SHAPE;
      WALL_SHAPE_PROPERTIES_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)));
   }
}
