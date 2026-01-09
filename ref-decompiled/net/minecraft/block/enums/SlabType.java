package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum SlabType implements StringIdentifiable {
   TOP("top"),
   BOTTOM("bottom"),
   DOUBLE("double");

   private final String name;

   private SlabType(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static SlabType[] method_36735() {
      return new SlabType[]{TOP, BOTTOM, DOUBLE};
   }
}
