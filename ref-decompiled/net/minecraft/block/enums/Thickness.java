package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum Thickness implements StringIdentifiable {
   TIP_MERGE("tip_merge"),
   TIP("tip"),
   FRUSTUM("frustum"),
   MIDDLE("middle"),
   BASE("base");

   private final String name;

   private Thickness(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static Thickness[] method_36728() {
      return new Thickness[]{TIP_MERGE, TIP, FRUSTUM, MIDDLE, BASE};
   }
}
