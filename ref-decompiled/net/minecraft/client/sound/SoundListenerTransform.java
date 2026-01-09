package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public record SoundListenerTransform(Vec3d position, Vec3d forward, Vec3d up) {
   public static final SoundListenerTransform DEFAULT;

   public SoundListenerTransform(Vec3d vec3d, Vec3d vec3d2, Vec3d vec3d3) {
      this.position = vec3d;
      this.forward = vec3d2;
      this.up = vec3d3;
   }

   public Vec3d right() {
      return this.forward.crossProduct(this.up);
   }

   public Vec3d position() {
      return this.position;
   }

   public Vec3d forward() {
      return this.forward;
   }

   public Vec3d up() {
      return this.up;
   }

   static {
      DEFAULT = new SoundListenerTransform(Vec3d.ZERO, new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 1.0, 0.0));
   }
}
