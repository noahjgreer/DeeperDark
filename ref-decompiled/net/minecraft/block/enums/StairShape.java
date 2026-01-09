package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum StairShape implements StringIdentifiable {
   STRAIGHT("straight"),
   INNER_LEFT("inner_left"),
   INNER_RIGHT("inner_right"),
   OUTER_LEFT("outer_left"),
   OUTER_RIGHT("outer_right");

   private final String name;

   private StairShape(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static StairShape[] method_36736() {
      return new StairShape[]{STRAIGHT, INNER_LEFT, INNER_RIGHT, OUTER_LEFT, OUTER_RIGHT};
   }
}
