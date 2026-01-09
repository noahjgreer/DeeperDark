package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum NavigationDirection {
   UP,
   DOWN,
   LEFT,
   RIGHT;

   private final IntComparator comparator = (a, b) -> {
      return a == b ? 0 : (this.isBefore(a, b) ? -1 : 1);
   };

   public NavigationAxis getAxis() {
      NavigationAxis var10000;
      switch (this.ordinal()) {
         case 0:
         case 1:
            var10000 = NavigationAxis.VERTICAL;
            break;
         case 2:
         case 3:
            var10000 = NavigationAxis.HORIZONTAL;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public NavigationDirection getOpposite() {
      NavigationDirection var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = DOWN;
            break;
         case 1:
            var10000 = UP;
            break;
         case 2:
            var10000 = RIGHT;
            break;
         case 3:
            var10000 = LEFT;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean isPositive() {
      boolean var10000;
      switch (this.ordinal()) {
         case 0:
         case 2:
            var10000 = false;
            break;
         case 1:
         case 3:
            var10000 = true;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public boolean isAfter(int a, int b) {
      if (this.isPositive()) {
         return a > b;
      } else {
         return b > a;
      }
   }

   public boolean isBefore(int a, int b) {
      if (this.isPositive()) {
         return a < b;
      } else {
         return b < a;
      }
   }

   public IntComparator getComparator() {
      return this.comparator;
   }

   // $FF: synthetic method
   private static NavigationDirection[] method_48244() {
      return new NavigationDirection[]{UP, DOWN, LEFT, RIGHT};
   }
}
