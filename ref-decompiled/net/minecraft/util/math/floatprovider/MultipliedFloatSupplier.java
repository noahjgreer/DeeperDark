package net.minecraft.util.math.floatprovider;

import java.util.Arrays;
import net.minecraft.util.math.random.Random;

public class MultipliedFloatSupplier implements FloatSupplier {
   private final FloatSupplier[] multipliers;

   public MultipliedFloatSupplier(FloatSupplier... multipliers) {
      this.multipliers = multipliers;
   }

   public float get(Random random) {
      float f = 1.0F;
      FloatSupplier[] var3 = this.multipliers;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         FloatSupplier floatSupplier = var3[var5];
         f *= floatSupplier.get(random);
      }

      return f;
   }

   public String toString() {
      return "MultipliedFloats" + Arrays.toString(this.multipliers);
   }
}
