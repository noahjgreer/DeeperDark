package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum ChestType implements StringIdentifiable {
   SINGLE("single"),
   LEFT("left"),
   RIGHT("right");

   private final String name;

   private ChestType(final String name) {
      this.name = name;
   }

   public String asString() {
      return this.name;
   }

   public ChestType getOpposite() {
      ChestType var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = SINGLE;
            break;
         case 1:
            var10000 = RIGHT;
            break;
         case 2:
            var10000 = LEFT;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static ChestType[] method_36724() {
      return new ChestType[]{SINGLE, LEFT, RIGHT};
   }
}
