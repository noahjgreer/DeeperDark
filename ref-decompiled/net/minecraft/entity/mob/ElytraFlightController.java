package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ElytraFlightController {
   private static final float field_54084 = 0.2617994F;
   private static final float field_54085 = -0.2617994F;
   private float leftWingPitch;
   private float leftWingYaw;
   private float leftWingRoll;
   private float lastLeftWingPitch;
   private float lastLeftWingYaw;
   private float lastLeftWingRoll;
   private final LivingEntity entity;

   public ElytraFlightController(LivingEntity entity) {
      this.entity = entity;
   }

   public void update() {
      this.lastLeftWingPitch = this.leftWingPitch;
      this.lastLeftWingYaw = this.leftWingYaw;
      this.lastLeftWingRoll = this.leftWingRoll;
      float g;
      float h;
      float i;
      if (this.entity.isGliding()) {
         float f = 1.0F;
         Vec3d vec3d = this.entity.getVelocity();
         if (vec3d.y < 0.0) {
            Vec3d vec3d2 = vec3d.normalize();
            f = 1.0F - (float)Math.pow(-vec3d2.y, 1.5);
         }

         g = MathHelper.lerp(f, 0.2617994F, 0.34906584F);
         h = MathHelper.lerp(f, -0.2617994F, -1.5707964F);
         i = 0.0F;
      } else if (this.entity.isInSneakingPose()) {
         g = 0.6981317F;
         h = -0.7853982F;
         i = 0.08726646F;
      } else {
         g = 0.2617994F;
         h = -0.2617994F;
         i = 0.0F;
      }

      this.leftWingPitch += (g - this.leftWingPitch) * 0.3F;
      this.leftWingYaw += (i - this.leftWingYaw) * 0.3F;
      this.leftWingRoll += (h - this.leftWingRoll) * 0.3F;
   }

   public float leftWingPitch(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastLeftWingPitch, this.leftWingPitch);
   }

   public float leftWingYaw(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastLeftWingYaw, this.leftWingYaw);
   }

   public float leftWingRoll(float tickProgress) {
      return MathHelper.lerp(tickProgress, this.lastLeftWingRoll, this.leftWingRoll);
   }
}
