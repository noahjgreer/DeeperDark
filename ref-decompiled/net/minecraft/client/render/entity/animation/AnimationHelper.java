package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class AnimationHelper {
   public static Vector3f createTranslationalVector(float x, float y, float z) {
      return new Vector3f(x, -y, z);
   }

   public static Vector3f createRotationalVector(float x, float y, float z) {
      return new Vector3f(x * 0.017453292F, y * 0.017453292F, z * 0.017453292F);
   }

   public static Vector3f createScalingVector(double x, double y, double z) {
      return new Vector3f((float)(x - 1.0), (float)(y - 1.0), (float)(z - 1.0));
   }
}
