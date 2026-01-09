package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BlockFace implements StringIdentifiable {
   FLOOR("floor"),
   WALL("wall"),
   CEILING("ceiling");

   private final String name;

   private BlockFace(final String name) {
      this.name = name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static BlockFace[] method_36720() {
      return new BlockFace[]{FLOOR, WALL, CEILING};
   }
}
