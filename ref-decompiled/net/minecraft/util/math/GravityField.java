package net.minecraft.util.math;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class GravityField {
   private final List points = Lists.newArrayList();

   public void addPoint(BlockPos pos, double mass) {
      if (mass != 0.0) {
         this.points.add(new Point(pos, mass));
      }

   }

   public double calculate(BlockPos pos, double mass) {
      if (mass == 0.0) {
         return 0.0;
      } else {
         double d = 0.0;

         Point point;
         for(Iterator var6 = this.points.iterator(); var6.hasNext(); d += point.getGravityFactor(pos)) {
            point = (Point)var6.next();
         }

         return d * mass;
      }
   }

   static class Point {
      private final BlockPos pos;
      private final double mass;

      public Point(BlockPos pos, double mass) {
         this.pos = pos;
         this.mass = mass;
      }

      public double getGravityFactor(BlockPos pos) {
         double d = this.pos.getSquaredDistance(pos);
         return d == 0.0 ? Double.POSITIVE_INFINITY : this.mass / Math.sqrt(d);
      }
   }
}
