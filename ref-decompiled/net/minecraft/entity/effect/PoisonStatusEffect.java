package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class PoisonStatusEffect extends StatusEffect {
   public static final int FLOWER_CONTACT_EFFECT_DURATION = 25;

   protected PoisonStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
      super(statusEffectCategory, i);
   }

   public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
      if (entity.getHealth() > 1.0F) {
         entity.damage(world, entity.getDamageSources().magic(), 1.0F);
      }

      return true;
   }

   public boolean canApplyUpdateEffect(int duration, int amplifier) {
      int i = 25 >> amplifier;
      if (i > 0) {
         return duration % i == 0;
      } else {
         return true;
      }
   }
}
