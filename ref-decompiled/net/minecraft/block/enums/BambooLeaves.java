package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BambooLeaves implements StringIdentifiable {
   NONE("none"),
   SMALL("small"),
   LARGE("large");

   private final String name;

   private BambooLeaves(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static BambooLeaves[] method_36721() {
      return new BambooLeaves[]{NONE, SMALL, LARGE};
   }
}
