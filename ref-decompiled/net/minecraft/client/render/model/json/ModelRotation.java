package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public record ModelRotation(Vector3f origin, Direction.Axis axis, float angle, boolean rescale) {
   public ModelRotation(Vector3f vector3f, Direction.Axis axis, float angle, boolean rescale) {
      this.origin = vector3f;
      this.axis = axis;
      this.angle = angle;
      this.rescale = rescale;
   }

   public Vector3f origin() {
      return this.origin;
   }

   public Direction.Axis axis() {
      return this.axis;
   }

   public float angle() {
      return this.angle;
   }

   public boolean rescale() {
      return this.rescale;
   }
}
