package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum WireConnection implements StringIdentifiable {
   UP("up"),
   SIDE("side"),
   NONE("none");

   private final String name;

   private WireConnection(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.asString();
   }

   public String asString() {
      return this.name;
   }

   public boolean isConnected() {
      return this != NONE;
   }

   // $FF: synthetic method
   private static WireConnection[] method_36733() {
      return new WireConnection[]{UP, SIDE, NONE};
   }
}
