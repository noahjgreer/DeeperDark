package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum CreakingHeartState implements StringIdentifiable {
   UPROOTED("uprooted"),
   DORMANT("dormant"),
   AWAKE("awake");

   private final String id;

   private CreakingHeartState(final String id) {
      this.id = id;
   }

   public String toString() {
      return this.id;
   }

   public String asString() {
      return this.id;
   }

   // $FF: synthetic method
   private static CreakingHeartState[] method_66479() {
      return new CreakingHeartState[]{UPROOTED, DORMANT, AWAKE};
   }
}
