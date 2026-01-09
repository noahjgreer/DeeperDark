package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public class HoldInHandsGoal extends Goal {
   private final MobEntity actor;
   private final ItemStack item;
   private final Predicate condition;
   @Nullable
   private final SoundEvent sound;

   public HoldInHandsGoal(MobEntity actor, ItemStack item, @Nullable SoundEvent sound, Predicate condition) {
      this.actor = actor;
      this.item = item;
      this.sound = sound;
      this.condition = condition;
   }

   public boolean canStart() {
      return this.condition.test(this.actor);
   }

   public boolean shouldContinue() {
      return this.actor.isUsingItem();
   }

   public void start() {
      this.actor.equipStack(EquipmentSlot.MAINHAND, this.item.copy());
      this.actor.setCurrentHand(Hand.MAIN_HAND);
   }

   public void stop() {
      this.actor.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      if (this.sound != null) {
         this.actor.playSound(this.sound, 1.0F, this.actor.getRandom().nextFloat() * 0.2F + 0.9F);
      }

   }
}
