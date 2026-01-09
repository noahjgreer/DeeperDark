package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum Attachment implements StringIdentifiable {
   FLOOR("floor"),
   CEILING("ceiling"),
   SINGLE_WALL("single_wall"),
   DOUBLE_WALL("double_wall");

   private final String name;

   private Attachment(final String name) {
      this.name = name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static Attachment[] method_36723() {
      return new Attachment[]{FLOOR, CEILING, SINGLE_WALL, DOUBLE_WALL};
   }
}
