package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class DirectionalLayoutWidget implements LayoutWidget {
   private final GridWidget grid;
   private final DisplayAxis axis;
   private int currentIndex;

   private DirectionalLayoutWidget(DisplayAxis axis) {
      this(0, 0, axis);
   }

   public DirectionalLayoutWidget(int x, int y, DisplayAxis axis) {
      this.currentIndex = 0;
      this.grid = new GridWidget(x, y);
      this.axis = axis;
   }

   public DirectionalLayoutWidget spacing(int spacing) {
      this.axis.setSpacing(this.grid, spacing);
      return this;
   }

   public Positioner copyPositioner() {
      return this.grid.copyPositioner();
   }

   public Positioner getMainPositioner() {
      return this.grid.getMainPositioner();
   }

   public Widget add(Widget widget, Positioner positioner) {
      return this.axis.add(this.grid, widget, this.currentIndex++, positioner);
   }

   public Widget add(Widget widget) {
      return this.add(widget, this.copyPositioner());
   }

   public Widget add(Widget widget, Consumer callback) {
      return this.axis.add(this.grid, widget, this.currentIndex++, (Positioner)Util.make(this.copyPositioner(), callback));
   }

   public void forEachElement(Consumer consumer) {
      this.grid.forEachElement(consumer);
   }

   public void refreshPositions() {
      this.grid.refreshPositions();
   }

   public int getWidth() {
      return this.grid.getWidth();
   }

   public int getHeight() {
      return this.grid.getHeight();
   }

   public void setX(int x) {
      this.grid.setX(x);
   }

   public void setY(int y) {
      this.grid.setY(y);
   }

   public int getX() {
      return this.grid.getX();
   }

   public int getY() {
      return this.grid.getY();
   }

   public static DirectionalLayoutWidget vertical() {
      return new DirectionalLayoutWidget(DirectionalLayoutWidget.DisplayAxis.VERTICAL);
   }

   public static DirectionalLayoutWidget horizontal() {
      return new DirectionalLayoutWidget(DirectionalLayoutWidget.DisplayAxis.HORIZONTAL);
   }

   @Environment(EnvType.CLIENT)
   public static enum DisplayAxis {
      HORIZONTAL,
      VERTICAL;

      void setSpacing(GridWidget grid, int spacing) {
         switch (this.ordinal()) {
            case 0:
               grid.setColumnSpacing(spacing);
               break;
            case 1:
               grid.setRowSpacing(spacing);
         }

      }

      public Widget add(GridWidget grid, Widget widget, int index, Positioner positioner) {
         Widget var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = (Widget)grid.add(widget, 0, index, (Positioner)positioner);
               break;
            case 1:
               var10000 = (Widget)grid.add(widget, index, 0, (Positioner)positioner);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static DisplayAxis[] method_52743() {
         return new DisplayAxis[]{HORIZONTAL, VERTICAL};
      }
   }
}
