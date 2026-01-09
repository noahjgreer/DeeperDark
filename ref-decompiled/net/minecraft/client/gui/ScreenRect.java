package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

@Environment(EnvType.CLIENT)
public record ScreenRect(ScreenPos position, int width, int height) {
   private static final ScreenRect EMPTY = new ScreenRect(0, 0, 0, 0);

   public ScreenRect(int sameAxis, int otherAxis, int width, int height) {
      this(new ScreenPos(sameAxis, otherAxis), width, height);
   }

   public ScreenRect(ScreenPos screenPos, int i, int j) {
      this.position = screenPos;
      this.width = i;
      this.height = j;
   }

   public static ScreenRect empty() {
      return EMPTY;
   }

   public static ScreenRect of(NavigationAxis axis, int sameAxisCoord, int otherAxisCoord, int sameAxisLength, int otherAxisLength) {
      ScreenRect var10000;
      switch (axis) {
         case HORIZONTAL:
            var10000 = new ScreenRect(sameAxisCoord, otherAxisCoord, sameAxisLength, otherAxisLength);
            break;
         case VERTICAL:
            var10000 = new ScreenRect(otherAxisCoord, sameAxisCoord, otherAxisLength, sameAxisLength);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public ScreenRect add(NavigationDirection direction) {
      return new ScreenRect(this.position.add(direction), this.width, this.height);
   }

   public int getLength(NavigationAxis axis) {
      int var10000;
      switch (axis) {
         case HORIZONTAL:
            var10000 = this.width;
            break;
         case VERTICAL:
            var10000 = this.height;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int getBoundingCoordinate(NavigationDirection direction) {
      NavigationAxis navigationAxis = direction.getAxis();
      return direction.isPositive() ? this.position.getComponent(navigationAxis) + this.getLength(navigationAxis) - 1 : this.position.getComponent(navigationAxis);
   }

   public ScreenRect getBorder(NavigationDirection direction) {
      int i = this.getBoundingCoordinate(direction);
      NavigationAxis navigationAxis = direction.getAxis().getOther();
      int j = this.getBoundingCoordinate(navigationAxis.getNegativeDirection());
      int k = this.getLength(navigationAxis);
      return of(direction.getAxis(), i, j, 1, k).add(direction);
   }

   public boolean overlaps(ScreenRect other) {
      return this.overlaps(other, NavigationAxis.HORIZONTAL) && this.overlaps(other, NavigationAxis.VERTICAL);
   }

   public boolean overlaps(ScreenRect other, NavigationAxis axis) {
      int i = this.getBoundingCoordinate(axis.getNegativeDirection());
      int j = other.getBoundingCoordinate(axis.getNegativeDirection());
      int k = this.getBoundingCoordinate(axis.getPositiveDirection());
      int l = other.getBoundingCoordinate(axis.getPositiveDirection());
      return Math.max(i, j) <= Math.min(k, l);
   }

   public int getCenter(NavigationAxis axis) {
      return (this.getBoundingCoordinate(axis.getPositiveDirection()) + this.getBoundingCoordinate(axis.getNegativeDirection())) / 2;
   }

   @Nullable
   public ScreenRect intersection(ScreenRect other) {
      int i = Math.max(this.getLeft(), other.getLeft());
      int j = Math.max(this.getTop(), other.getTop());
      int k = Math.min(this.getRight(), other.getRight());
      int l = Math.min(this.getBottom(), other.getBottom());
      return i < k && j < l ? new ScreenRect(i, j, k - i, l - j) : null;
   }

   public boolean intersects(ScreenRect other) {
      return this.getLeft() < other.getRight() && this.getRight() > other.getLeft() && this.getTop() < other.getBottom() && this.getBottom() > other.getTop();
   }

   public boolean contains(ScreenRect other) {
      return other.getLeft() >= this.getLeft() && other.getTop() >= this.getTop() && other.getRight() <= this.getRight() && other.getBottom() <= this.getBottom();
   }

   public int getTop() {
      return this.position.y();
   }

   public int getBottom() {
      return this.position.y() + this.height;
   }

   public int getLeft() {
      return this.position.x();
   }

   public int getRight() {
      return this.position.x() + this.width;
   }

   public boolean contains(int x, int y) {
      return x >= this.getLeft() && x < this.getRight() && y >= this.getTop() && y < this.getBottom();
   }

   public ScreenRect transform(Matrix3x2f transformation) {
      Vector2f vector2f = transformation.transformPosition((float)this.getLeft(), (float)this.getTop(), new Vector2f());
      Vector2f vector2f2 = transformation.transformPosition((float)this.getRight(), (float)this.getBottom(), new Vector2f());
      return new ScreenRect(MathHelper.floor(vector2f.x), MathHelper.floor(vector2f.y), MathHelper.floor(vector2f2.x - vector2f.x), MathHelper.floor(vector2f2.y - vector2f.y));
   }

   public ScreenRect transformEachVertex(Matrix3x2f transformation) {
      Vector2f vector2f = transformation.transformPosition((float)this.getLeft(), (float)this.getTop(), new Vector2f());
      Vector2f vector2f2 = transformation.transformPosition((float)this.getRight(), (float)this.getTop(), new Vector2f());
      Vector2f vector2f3 = transformation.transformPosition((float)this.getLeft(), (float)this.getBottom(), new Vector2f());
      Vector2f vector2f4 = transformation.transformPosition((float)this.getRight(), (float)this.getBottom(), new Vector2f());
      float f = Math.min(Math.min(vector2f.x(), vector2f3.x()), Math.min(vector2f2.x(), vector2f4.x()));
      float g = Math.max(Math.max(vector2f.x(), vector2f3.x()), Math.max(vector2f2.x(), vector2f4.x()));
      float h = Math.min(Math.min(vector2f.y(), vector2f3.y()), Math.min(vector2f2.y(), vector2f4.y()));
      float i = Math.max(Math.max(vector2f.y(), vector2f3.y()), Math.max(vector2f2.y(), vector2f4.y()));
      return new ScreenRect(MathHelper.floor(f), MathHelper.floor(h), MathHelper.ceil(g - f), MathHelper.ceil(i - h));
   }

   public ScreenPos position() {
      return this.position;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }
}
