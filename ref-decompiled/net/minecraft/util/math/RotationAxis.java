package net.minecraft.util.math;

import org.joml.Quaternionf;
import org.joml.Vector3f;

@FunctionalInterface
public interface RotationAxis {
   RotationAxis NEGATIVE_X = (rad) -> {
      return (new Quaternionf()).rotationX(-rad);
   };
   RotationAxis POSITIVE_X = (rad) -> {
      return (new Quaternionf()).rotationX(rad);
   };
   RotationAxis NEGATIVE_Y = (rad) -> {
      return (new Quaternionf()).rotationY(-rad);
   };
   RotationAxis POSITIVE_Y = (rad) -> {
      return (new Quaternionf()).rotationY(rad);
   };
   RotationAxis NEGATIVE_Z = (rad) -> {
      return (new Quaternionf()).rotationZ(-rad);
   };
   RotationAxis POSITIVE_Z = (rad) -> {
      return (new Quaternionf()).rotationZ(rad);
   };

   static RotationAxis of(Vector3f axis) {
      return (rad) -> {
         return (new Quaternionf()).rotationAxis(rad, axis);
      };
   }

   Quaternionf rotation(float rad);

   default Quaternionf rotationDegrees(float deg) {
      return this.rotation(deg * 0.017453292F);
   }
}
