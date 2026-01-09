package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EmptyWidget implements Widget {
   private int x;
   private int y;
   private final int width;
   private final int height;

   public EmptyWidget(int width, int height) {
      this(0, 0, width, height);
   }

   public EmptyWidget(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public static EmptyWidget ofWidth(int width) {
      return new EmptyWidget(width, 0);
   }

   public static EmptyWidget ofHeight(int height) {
      return new EmptyWidget(0, height);
   }

   public void setX(int x) {
      this.x = x;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void forEachChild(Consumer consumer) {
   }
}
