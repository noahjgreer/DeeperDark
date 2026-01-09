package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AnimationState;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class CamelEntityRenderState extends LivingEntityRenderState {
   public ItemStack saddleStack;
   public boolean hasPassengers;
   public float jumpCooldown;
   public final AnimationState sittingTransitionAnimationState;
   public final AnimationState sittingAnimationState;
   public final AnimationState standingTransitionAnimationState;
   public final AnimationState idlingAnimationState;
   public final AnimationState dashingAnimationState;

   public CamelEntityRenderState() {
      this.saddleStack = ItemStack.EMPTY;
      this.sittingTransitionAnimationState = new AnimationState();
      this.sittingAnimationState = new AnimationState();
      this.standingTransitionAnimationState = new AnimationState();
      this.idlingAnimationState = new AnimationState();
      this.dashingAnimationState = new AnimationState();
   }
}
