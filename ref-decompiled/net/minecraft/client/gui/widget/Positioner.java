package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Positioner {
   Positioner margin(int value);

   Positioner margin(int x, int y);

   Positioner margin(int left, int top, int right, int bottom);

   Positioner marginLeft(int marginLeft);

   Positioner marginTop(int marginTop);

   Positioner marginRight(int marginRight);

   Positioner marginBottom(int marginBottom);

   Positioner marginX(int marginX);

   Positioner marginY(int marginY);

   Positioner relative(float x, float y);

   Positioner relativeX(float relativeX);

   Positioner relativeY(float relativeY);

   default Positioner alignLeft() {
      return this.relativeX(0.0F);
   }

   default Positioner alignHorizontalCenter() {
      return this.relativeX(0.5F);
   }

   default Positioner alignRight() {
      return this.relativeX(1.0F);
   }

   default Positioner alignTop() {
      return this.relativeY(0.0F);
   }

   default Positioner alignVerticalCenter() {
      return this.relativeY(0.5F);
   }

   default Positioner alignBottom() {
      return this.relativeY(1.0F);
   }

   Positioner copy();

   Impl toImpl();

   static Positioner create() {
      return new Impl();
   }

   @Environment(EnvType.CLIENT)
   public static class Impl implements Positioner {
      public int marginLeft;
      public int marginTop;
      public int marginRight;
      public int marginBottom;
      public float relativeX;
      public float relativeY;

      public Impl() {
      }

      public Impl(Impl original) {
         this.marginLeft = original.marginLeft;
         this.marginTop = original.marginTop;
         this.marginRight = original.marginRight;
         this.marginBottom = original.marginBottom;
         this.relativeX = original.relativeX;
         this.relativeY = original.relativeY;
      }

      public Impl margin(int i) {
         return this.margin(i, i);
      }

      public Impl margin(int i, int j) {
         return this.marginX(i).marginY(j);
      }

      public Impl margin(int i, int j, int k, int l) {
         return this.marginLeft(i).marginRight(k).marginTop(j).marginBottom(l);
      }

      public Impl marginLeft(int i) {
         this.marginLeft = i;
         return this;
      }

      public Impl marginTop(int i) {
         this.marginTop = i;
         return this;
      }

      public Impl marginRight(int i) {
         this.marginRight = i;
         return this;
      }

      public Impl marginBottom(int i) {
         this.marginBottom = i;
         return this;
      }

      public Impl marginX(int i) {
         return this.marginLeft(i).marginRight(i);
      }

      public Impl marginY(int i) {
         return this.marginTop(i).marginBottom(i);
      }

      public Impl relative(float f, float g) {
         this.relativeX = f;
         this.relativeY = g;
         return this;
      }

      public Impl relativeX(float f) {
         this.relativeX = f;
         return this;
      }

      public Impl relativeY(float f) {
         this.relativeY = f;
         return this;
      }

      public Impl copy() {
         return new Impl(this);
      }

      public Impl toImpl() {
         return this;
      }

      // $FF: synthetic method
      public Positioner copy() {
         return this.copy();
      }

      // $FF: synthetic method
      public Positioner relativeY(final float relativeY) {
         return this.relativeY(relativeY);
      }

      // $FF: synthetic method
      public Positioner relativeX(final float relativeX) {
         return this.relativeX(relativeX);
      }

      // $FF: synthetic method
      public Positioner relative(final float x, final float y) {
         return this.relative(x, y);
      }

      // $FF: synthetic method
      public Positioner marginY(final int marginY) {
         return this.marginY(marginY);
      }

      // $FF: synthetic method
      public Positioner marginX(final int marginX) {
         return this.marginX(marginX);
      }

      // $FF: synthetic method
      public Positioner marginBottom(final int marginBottom) {
         return this.marginBottom(marginBottom);
      }

      // $FF: synthetic method
      public Positioner marginRight(final int marginRight) {
         return this.marginRight(marginRight);
      }

      // $FF: synthetic method
      public Positioner marginTop(final int marginTop) {
         return this.marginTop(marginTop);
      }

      // $FF: synthetic method
      public Positioner marginLeft(final int marginLeft) {
         return this.marginLeft(marginLeft);
      }

      // $FF: synthetic method
      public Positioner margin(final int left, final int top, final int right, final int bottom) {
         return this.margin(left, top, right, bottom);
      }

      // $FF: synthetic method
      public Positioner margin(final int x, final int y) {
         return this.margin(x, y);
      }

      // $FF: synthetic method
      public Positioner margin(final int value) {
         return this.margin(value);
      }
   }
}
