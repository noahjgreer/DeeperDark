package net.minecraft.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum SculkSensorPhase implements StringIdentifiable {
   INACTIVE("inactive"),
   ACTIVE("active"),
   COOLDOWN("cooldown");

   private final String name;

   private SculkSensorPhase(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static SculkSensorPhase[] method_36734() {
      return new SculkSensorPhase[]{INACTIVE, ACTIVE, COOLDOWN};
   }
}
