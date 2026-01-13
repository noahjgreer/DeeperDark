/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jspecify.annotations.Nullable;

class IllusionerEntity.GiveInvisibilityGoal
extends SpellcastingIllagerEntity.CastSpellGoal {
    IllusionerEntity.GiveInvisibilityGoal() {
        super(IllusionerEntity.this);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart()) {
            return false;
        }
        return !IllusionerEntity.this.hasStatusEffect(StatusEffects.INVISIBILITY);
    }

    @Override
    protected int getSpellTicks() {
        return 20;
    }

    @Override
    protected int startTimeDelay() {
        return 340;
    }

    @Override
    protected void castSpell() {
        IllusionerEntity.this.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200));
    }

    @Override
    protected @Nullable SoundEvent getSoundPrepare() {
        return SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR;
    }

    @Override
    protected SpellcastingIllagerEntity.Spell getSpell() {
        return SpellcastingIllagerEntity.Spell.DISAPPEAR;
    }
}
