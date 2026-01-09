package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

public enum DoubleBlockHalf implements StringIdentifiable {
   UPPER(Direction.DOWN),
   LOWER(Direction.UP);

   private final Direction oppositeDirection;

   private DoubleBlockHalf(final Direction oppositeDirection) {
      this.oppositeDirection = oppositeDirection;
   }

   public Direction getOppositeDirection() {
      return this.oppositeDirection;
   }

   public String toString() {
      return this.asString();
   }

   public String asString() {
      return this == UPPER ? "upper" : "lower";
   }

   public DoubleBlockHalf getOtherHalf() {
      return this == UPPER ? LOWER : UPPER;
   }

   // $FF: synthetic method
   private static DoubleBlockHalf[] method_36727() {
      return new DoubleBlockHalf[]{UPPER, LOWER};
   }
}
