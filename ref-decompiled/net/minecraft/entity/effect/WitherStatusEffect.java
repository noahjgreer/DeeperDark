package net.minecraft.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public class WitherStatusEffect extends StatusEffect {
   public static final int FLOWER_CONTACT_EFFECT_DURATION = 40;

   protected WitherStatusEffect(StatusEffectCategory statusEffectCategory, int i) {
      super(statusEffectCategory, i);
   }

   public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
      entity.damage(world, entity.getDamageSources().wither(), 1.0F);
      return true;
   }

   public boolean canApplyUpdateEffect(int duration, int amplifier) {
      int i = 40 >> amplifier;
      if (i > 0) {
         return duration % i == 0;
      } else {
         return true;
      }
   }
}
