package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.EulerAngle;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityRenderState extends BipedEntityRenderState {
   public float yaw;
   public float timeSinceLastHit;
   public boolean marker;
   public boolean small;
   public boolean showArms;
   public boolean showBasePlate = true;
   public EulerAngle headRotation;
   public EulerAngle bodyRotation;
   public EulerAngle leftArmRotation;
   public EulerAngle rightArmRotation;
   public EulerAngle leftLegRotation;
   public EulerAngle rightLegRotation;

   public ArmorStandEntityRenderState() {
      this.headRotation = ArmorStandEntity.DEFAULT_HEAD_ROTATION;
      this.bodyRotation = ArmorStandEntity.DEFAULT_BODY_ROTATION;
      this.leftArmRotation = ArmorStandEntity.DEFAULT_LEFT_ARM_ROTATION;
      this.rightArmRotation = ArmorStandEntity.DEFAULT_RIGHT_ARM_ROTATION;
      this.leftLegRotation = ArmorStandEntity.DEFAULT_LEFT_LEG_ROTATION;
      this.rightLegRotation = ArmorStandEntity.DEFAULT_RIGHT_LEG_ROTATION;
   }
}
