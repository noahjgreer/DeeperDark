package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public enum CubeFace {
   DOWN(new Corner[]{new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH)}),
   UP(new Corner[]{new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)}),
   NORTH(new Corner[]{new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)}),
   SOUTH(new Corner[]{new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH)}),
   WEST(new Corner[]{new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.WEST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH)}),
   EAST(new Corner[]{new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.SOUTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.DOWN, CubeFace.DirectionIds.NORTH), new Corner(CubeFace.DirectionIds.EAST, CubeFace.DirectionIds.UP, CubeFace.DirectionIds.NORTH)});

   private static final CubeFace[] DIRECTION_LOOKUP = (CubeFace[])Util.make(new CubeFace[6], (lookup) -> {
      lookup[CubeFace.DirectionIds.DOWN] = DOWN;
      lookup[CubeFace.DirectionIds.UP] = UP;
      lookup[CubeFace.DirectionIds.NORTH] = NORTH;
      lookup[CubeFace.DirectionIds.SOUTH] = SOUTH;
      lookup[CubeFace.DirectionIds.WEST] = WEST;
      lookup[CubeFace.DirectionIds.EAST] = EAST;
   });
   private final Corner[] corners;

   public static CubeFace getFace(Direction direction) {
      return DIRECTION_LOOKUP[direction.getIndex()];
   }

   private CubeFace(final Corner... corners) {
      this.corners = corners;
   }

   public Corner getCorner(int corner) {
      return this.corners[corner];
   }

   // $FF: synthetic method
   private static CubeFace[] method_36913() {
      return new CubeFace[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   }

   @Environment(EnvType.CLIENT)
   public static class Corner {
      public final int xSide;
      public final int ySide;
      public final int zSide;

      Corner(int xSide, int ySide, int zSide) {
         this.xSide = xSide;
         this.ySide = ySide;
         this.zSide = zSide;
      }
   }

   @Environment(EnvType.CLIENT)
   public static final class DirectionIds {
      public static final int SOUTH;
      public static final int UP;
      public static final int EAST;
      public static final int NORTH;
      public static final int DOWN;
      public static final int WEST;

      static {
         SOUTH = Direction.SOUTH.getIndex();
         UP = Direction.UP.getIndex();
         EAST = Direction.EAST.getIndex();
         NORTH = Direction.NORTH.getIndex();
         DOWN = Direction.DOWN.getIndex();
         WEST = Direction.WEST.getIndex();
      }
   }
}
