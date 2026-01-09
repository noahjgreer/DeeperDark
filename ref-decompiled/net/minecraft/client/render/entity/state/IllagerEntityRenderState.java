package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class IllagerEntityRenderState extends ArmedEntityRenderState {
   public boolean hasVehicle;
   public boolean attacking;
   public Arm illagerMainArm;
   public IllagerEntity.State illagerState;
   public int crossbowPullTime;
   public int itemUseTime;
   public float handSwingProgress;

   public IllagerEntityRenderState() {
      this.illagerMainArm = Arm.RIGHT;
      this.illagerState = IllagerEntity.State.NEUTRAL;
   }
}
