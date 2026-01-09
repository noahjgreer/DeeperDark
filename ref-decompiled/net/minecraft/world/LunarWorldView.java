package net.minecraft.world;

import net.minecraft.world.dimension.DimensionType;

public interface LunarWorldView extends WorldView {
   long getLunarTime();

   default float getMoonSize() {
      return DimensionType.MOON_SIZES[this.getDimension().getMoonPhase(this.getLunarTime())];
   }

   default float getSkyAngle(float tickProgress) {
      return this.getDimension().getSkyAngle(this.getLunarTime());
   }

   default int getMoonPhase() {
      return this.getDimension().getMoonPhase(this.getLunarTime());
   }
}
