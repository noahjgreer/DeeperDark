package net.minecraft.entity.ai.control;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

public class AquaticMoveControl extends MoveControl {
   private static final float field_40123 = 10.0F;
   private static final float field_40124 = 60.0F;
   private final int pitchChange;
   private final int yawChange;
   private final float speedInWater;
   private final float speedInAir;
   private final boolean buoyant;

   public AquaticMoveControl(MobEntity entity, int pitchChange, int yawChange, float speedInWater, float speedInAir, boolean buoyant) {
      super(entity);
      this.pitchChange = pitchChange;
      this.yawChange = yawChange;
      this.speedInWater = speedInWater;
      this.speedInAir = speedInAir;
      this.buoyant = buoyant;
   }

   public void tick() {
      if (this.buoyant && this.entity.isTouchingWater()) {
         this.entity.setVelocity(this.entity.getVelocity().add(0.0, 0.005, 0.0));
      }

      if (this.state == MoveControl.State.MOVE_TO && !this.entity.getNavigation().isIdle()) {
         double d = this.targetX - this.entity.getX();
         double e = this.targetY - this.entity.getY();
         double f = this.targetZ - this.entity.getZ();
         double g = d * d + e * e + f * f;
         if (g < 2.500000277905201E-7) {
            this.entity.setForwardSpeed(0.0F);
         } else {
            float h = (float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F;
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), h, (float)this.yawChange));
            this.entity.bodyYaw = this.entity.getYaw();
            this.entity.headYaw = this.entity.getYaw();
            float i = (float)(this.speed * this.entity.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
            if (this.entity.isTouchingWater()) {
               this.entity.setMovementSpeed(i * this.speedInWater);
               double j = Math.sqrt(d * d + f * f);
               float k;
               if (Math.abs(e) > 9.999999747378752E-6 || Math.abs(j) > 9.999999747378752E-6) {
                  k = -((float)(MathHelper.atan2(e, j) * 57.2957763671875));
                  k = MathHelper.clamp(MathHelper.wrapDegrees(k), (float)(-this.pitchChange), (float)this.pitchChange);
                  this.entity.setPitch(this.changeAngle(this.entity.getPitch(), k, 5.0F));
               }

               k = MathHelper.cos(this.entity.getPitch() * 0.017453292F);
               float l = MathHelper.sin(this.entity.getPitch() * 0.017453292F);
               this.entity.forwardSpeed = k * i;
               this.entity.upwardSpeed = -l * i;
            } else {
               float m = Math.abs(MathHelper.wrapDegrees(this.entity.getYaw() - h));
               float n = method_45335(m);
               this.entity.setMovementSpeed(i * this.speedInAir * n);
            }

         }
      } else {
         this.entity.setMovementSpeed(0.0F);
         this.entity.setSidewaysSpeed(0.0F);
         this.entity.setUpwardSpeed(0.0F);
         this.entity.setForwardSpeed(0.0F);
      }
   }

   private static float method_45335(float f) {
      return 1.0F - MathHelper.clamp((f - 10.0F) / 50.0F, 0.0F, 1.0F);
   }
}
