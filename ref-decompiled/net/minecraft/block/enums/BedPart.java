package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BedPart implements StringIdentifiable {
   HEAD("head"),
   FOOT("foot");

   private final String name;

   private BedPart(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static BedPart[] method_36722() {
      return new BedPart[]{HEAD, FOOT};
   }
}
