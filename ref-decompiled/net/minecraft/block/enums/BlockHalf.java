package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BlockHalf implements StringIdentifiable {
   TOP("top"),
   BOTTOM("bottom");

   private final String name;

   private BlockHalf(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static BlockHalf[] method_36729() {
      return new BlockHalf[]{TOP, BOTTOM};
   }
}
