package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public class BipedEntityRenderState extends ArmedEntityRenderState {
   public float leaningPitch;
   public float handSwingProgress;
   public float limbAmplitudeInverse = 1.0F;
   public float crossbowPullTime;
   public int itemUseTime;
   public Arm preferredArm;
   public Hand activeHand;
   public boolean isInSneakingPose;
   public boolean isGliding;
   public boolean isSwimming;
   public boolean hasVehicle;
   public boolean isUsingItem;
   public float leftWingPitch;
   public float leftWingYaw;
   public float leftWingRoll;
   public ItemStack equippedHeadStack;
   public ItemStack equippedChestStack;
   public ItemStack equippedLegsStack;
   public ItemStack equippedFeetStack;

   public BipedEntityRenderState() {
      this.preferredArm = Arm.RIGHT;
      this.activeHand = Hand.MAIN_HAND;
      this.equippedHeadStack = ItemStack.EMPTY;
      this.equippedChestStack = ItemStack.EMPTY;
      this.equippedLegsStack = ItemStack.EMPTY;
      this.equippedFeetStack = ItemStack.EMPTY;
   }
}
