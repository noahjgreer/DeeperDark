package net.noahsarch.deeperdark;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.LivingEntity;

public class DeeperDarkStatusEffects {
    public static class ScentlessStatusEffect extends StatusEffect {
        public ScentlessStatusEffect() {
            super(StatusEffectCategory.BENEFICIAL, 0x838CDC); // Color code for the effect
        }

        @Override
        public boolean canApplyUpdateEffect(int duration, int amplifier) {
            return duration % 20 == 0; // Apply effect every second
        }

        @Override
        public boolean applyUpdateEffect(net.minecraft.server.world.ServerWorld world, LivingEntity entity, int amplifier) {
            // No additional effect logic needed for now
            return true;
        }
    }
}
