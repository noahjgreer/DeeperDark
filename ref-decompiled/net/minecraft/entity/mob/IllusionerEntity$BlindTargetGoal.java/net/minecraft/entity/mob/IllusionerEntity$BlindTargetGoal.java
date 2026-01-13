/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Difficulty;

class IllusionerEntity.BlindTargetGoal
extends SpellcastingIllagerEntity.CastSpellGoal {
    private int targetId;

    IllusionerEntity.BlindTargetGoal() {
        super(IllusionerEntity.this);
    }

    @Override
    public boolean canStart() {
        if (!super.canStart()) {
            return false;
        }
        if (IllusionerEntity.this.getTarget() == null) {
            return false;
        }
        if (IllusionerEntity.this.getTarget().getId() == this.targetId) {
            return false;
        }
        return IllusionerEntity.BlindTargetGoal.getServerWorld(IllusionerEntity.this).getLocalDifficulty(IllusionerEntity.this.getBlockPos()).isHarderThan(Difficulty.NORMAL.ordinal());
    }

    @Override
    public void start() {
        super.start();
        LivingEntity livingEntity = IllusionerEntity.this.getTarget();
        if (livingEntity != null) {
            this.targetId = livingEntity.getId();
        }
    }

    @Override
    protected int getSpellTicks() {
        return 20;
    }

    @Override
    protected int startTimeDelay() {
        return 180;
    }

    @Override
    protected void castSpell() {
        IllusionerEntity.this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 400), IllusionerEntity.this);
    }

    @Override
    protected SoundEvent getSoundPrepare() {
        return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
    }

    @Override
    protected SpellcastingIllagerEntity.Spell getSpell() {
        return SpellcastingIllagerEntity.Spell.BLINDNESS;
    }
}
