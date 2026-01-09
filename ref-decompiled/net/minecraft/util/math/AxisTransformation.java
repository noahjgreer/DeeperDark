package net.minecraft.util.math;

import java.util.Arrays;
import net.minecraft.util.Util;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;

public enum AxisTransformation {
   P123(0, 1, 2),
   P213(1, 0, 2),
   P132(0, 2, 1),
   P231(1, 2, 0),
   P312(2, 0, 1),
   P321(2, 1, 0);

   private final int[] mappings;
   private final Matrix3fc matrix;
   private static final int NUM_AXES = 3;
   private static final AxisTransformation[][] COMBINATIONS = (AxisTransformation[][])Util.make(new AxisTransformation[values().length][values().length], (combinations) -> {
      AxisTransformation[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         AxisTransformation axisTransformation = var1[var3];
         AxisTransformation[] var5 = values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            AxisTransformation axisTransformation2 = var5[var7];
            int[] is = new int[3];

            for(int i = 0; i < 3; ++i) {
               is[i] = axisTransformation.mappings[axisTransformation2.mappings[i]];
            }

            AxisTransformation axisTransformation3 = (AxisTransformation)Arrays.stream(values()).filter((transformation) -> {
               return Arrays.equals(transformation.mappings, is);
            }).findFirst().get();
            combinations[axisTransformation.ordinal()][axisTransformation2.ordinal()] = axisTransformation3;
         }
      }

   });

   private AxisTransformation(final int xMapping, final int yMapping, final int zMapping) {
      this.mappings = new int[]{xMapping, yMapping, zMapping};
      Matrix3f matrix3f = (new Matrix3f()).zero();
      matrix3f.set(this.map(0), 0, 1.0F);
      matrix3f.set(this.map(1), 1, 1.0F);
      matrix3f.set(this.map(2), 2, 1.0F);
      this.matrix = matrix3f;
   }

   public AxisTransformation prepend(AxisTransformation transformation) {
      return COMBINATIONS[this.ordinal()][transformation.ordinal()];
   }

   public int map(int oldAxis) {
      return this.mappings[oldAxis];
   }

   public Matrix3fc getMatrix() {
      return this.matrix;
   }

   // $FF: synthetic method
   private static AxisTransformation[] method_36937() {
      return new AxisTransformation[]{P123, P213, P132, P231, P312, P321};
   }
}
