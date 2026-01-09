package net.minecraft.client.gui.navigation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface GuiNavigation {
   NavigationDirection getDirection();

   @Environment(EnvType.CLIENT)
   public static record Arrow(NavigationDirection direction) implements GuiNavigation {
      public Arrow(NavigationDirection navigationDirection) {
         this.direction = navigationDirection;
      }

      public NavigationDirection getDirection() {
         return this.direction.getAxis() == NavigationAxis.VERTICAL ? this.direction : NavigationDirection.DOWN;
      }

      public NavigationDirection direction() {
         return this.direction;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Down implements GuiNavigation {
      public NavigationDirection getDirection() {
         return NavigationDirection.DOWN;
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Tab(boolean forward) implements GuiNavigation {
      public Tab(boolean bl) {
         this.forward = bl;
      }

      public NavigationDirection getDirection() {
         return this.forward ? NavigationDirection.DOWN : NavigationDirection.UP;
      }

      public boolean forward() {
         return this.forward;
      }
   }
}
