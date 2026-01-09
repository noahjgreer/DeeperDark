package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public abstract class ConnectingBlock extends Block {
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   public static final BooleanProperty UP;
   public static final BooleanProperty DOWN;
   public static final Map FACING_PROPERTIES;
   private final Function shapeFunction;

   protected ConnectingBlock(float radius, AbstractBlock.Settings settings) {
      super(settings);
      this.shapeFunction = this.createShapeFunction(radius);
   }

   protected abstract MapCodec getCodec();

   private Function createShapeFunction(float radius) {
      VoxelShape voxelShape = Block.createCubeShape((double)radius);
      Map map = VoxelShapes.createFacingShapeMap(Block.createCuboidZShape((double)radius, 0.0, 8.0));
      return this.createShapeFunction((state) -> {
         VoxelShape voxelShape2 = voxelShape;
         Iterator var4 = FACING_PROPERTIES.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            if ((Boolean)state.get((Property)entry.getValue())) {
               voxelShape2 = VoxelShapes.union((VoxelShape)map.get(entry.getKey()), voxelShape2);
            }
         }

         return voxelShape2;
      });
   }

   protected boolean isTransparent(BlockState state) {
      return false;
   }

   protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return (VoxelShape)this.shapeFunction.apply(state);
   }

   static {
      NORTH = Properties.NORTH;
      EAST = Properties.EAST;
      SOUTH = Properties.SOUTH;
      WEST = Properties.WEST;
      UP = Properties.UP;
      DOWN = Properties.DOWN;
      FACING_PROPERTIES = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST, Direction.UP, UP, Direction.DOWN, DOWN)));
   }
}
