package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record EntityHitbox(double x0, double y0, double z0, double x1, double y1, double z1, float offsetX, float offsetY, float offsetZ, float red, float green, float blue) {
   public EntityHitbox(double x0, double y0, double z0, double x1, double y1, double z1, float red, float green, float blue) {
      this(x0, y0, z0, x1, y1, z1, 0.0F, 0.0F, 0.0F, red, green, blue);
   }

   public EntityHitbox(double d, double e, double f, double g, double h, double i, float j, float k, float l, float m, float n, float o) {
      this.x0 = d;
      this.y0 = e;
      this.z0 = f;
      this.x1 = g;
      this.y1 = h;
      this.z1 = i;
      this.offsetX = j;
      this.offsetY = k;
      this.offsetZ = l;
      this.red = m;
      this.green = n;
      this.blue = o;
   }

   public double x0() {
      return this.x0;
   }

   public double y0() {
      return this.y0;
   }

   public double z0() {
      return this.z0;
   }

   public double x1() {
      return this.x1;
   }

   public double y1() {
      return this.y1;
   }

   public double z1() {
      return this.z1;
   }

   public float offsetX() {
      return this.offsetX;
   }

   public float offsetY() {
      return this.offsetY;
   }

   public float offsetZ() {
      return this.offsetZ;
   }

   public float red() {
      return this.red;
   }

   public float green() {
      return this.green;
   }

   public float blue() {
      return this.blue;
   }
}
