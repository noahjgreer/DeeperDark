/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.sound.SoundEvent;
import org.jspecify.annotations.Nullable;

protected abstract class SpellcastingIllagerEntity.CastSpellGoal
extends Goal {
    protected int spellCooldown;
    protected int startTime;

    protected SpellcastingIllagerEntity.CastSpellGoal() {
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = SpellcastingIllagerEntity.this.getTarget();
        if (livingEntity == null || !livingEntity.isAlive()) {
            return false;
        }
        if (SpellcastingIllagerEntity.this.isSpellcasting()) {
            return false;
        }
        return SpellcastingIllagerEntity.this.age >= this.startTime;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity livingEntity = SpellcastingIllagerEntity.this.getTarget();
        return livingEntity != null && livingEntity.isAlive() && this.spellCooldown > 0;
    }

    @Override
    public void start() {
        this.spellCooldown = this.getTickCount(this.getInitialCooldown());
        SpellcastingIllagerEntity.this.spellTicks = this.getSpellTicks();
        this.startTime = SpellcastingIllagerEntity.this.age + this.startTimeDelay();
        SoundEvent soundEvent = this.getSoundPrepare();
        if (soundEvent != null) {
            SpellcastingIllagerEntity.this.playSound(soundEvent, 1.0f, 1.0f);
        }
        SpellcastingIllagerEntity.this.setSpell(this.getSpell());
    }

    @Override
    public void tick() {
        --this.spellCooldown;
        if (this.spellCooldown == 0) {
            this.castSpell();
            SpellcastingIllagerEntity.this.playSound(SpellcastingIllagerEntity.this.getCastSpellSound(), 1.0f, 1.0f);
        }
    }

    protected abstract void castSpell();

    protected int getInitialCooldown() {
        return 20;
    }

    protected abstract int getSpellTicks();

    protected abstract int startTimeDelay();

    protected abstract @Nullable SoundEvent getSoundPrepare();

    protected abstract SpellcastingIllagerEntity.Spell getSpell();
}
