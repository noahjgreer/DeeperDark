package net.minecraft.util.math;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum EightWayDirection {
   NORTH(new Direction[]{Direction.NORTH}),
   NORTH_EAST(new Direction[]{Direction.NORTH, Direction.EAST}),
   EAST(new Direction[]{Direction.EAST}),
   SOUTH_EAST(new Direction[]{Direction.SOUTH, Direction.EAST}),
   SOUTH(new Direction[]{Direction.SOUTH}),
   SOUTH_WEST(new Direction[]{Direction.SOUTH, Direction.WEST}),
   WEST(new Direction[]{Direction.WEST}),
   NORTH_WEST(new Direction[]{Direction.NORTH, Direction.WEST});

   private final Set directions;
   private final Vec3i offset;

   private EightWayDirection(final Direction... directions) {
      this.directions = Sets.immutableEnumSet(Arrays.asList(directions));
      this.offset = new Vec3i(0, 0, 0);
      Direction[] var4 = directions;
      int var5 = directions.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction direction = var4[var6];
         this.offset.setX(this.offset.getX() + direction.getOffsetX()).setY(this.offset.getY() + direction.getOffsetY()).setZ(this.offset.getZ() + direction.getOffsetZ());
      }

   }

   public Set getDirections() {
      return this.directions;
   }

   public int getOffsetX() {
      return this.offset.getX();
   }

   public int getOffsetZ() {
      return this.offset.getZ();
   }

   // $FF: synthetic method
   private static EightWayDirection[] method_36935() {
      return new EightWayDirection[]{NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST};
   }
}
