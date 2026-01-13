/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;

protected class SpellcastingIllagerEntity.LookAtTargetGoal
extends Goal {
    public SpellcastingIllagerEntity.LookAtTargetGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return SpellcastingIllagerEntity.this.getSpellTicks() > 0;
    }

    @Override
    public void start() {
        super.start();
        SpellcastingIllagerEntity.this.navigation.stop();
    }

    @Override
    public void stop() {
        super.stop();
        SpellcastingIllagerEntity.this.setSpell(SpellcastingIllagerEntity.Spell.NONE);
    }

    @Override
    public void tick() {
        if (SpellcastingIllagerEntity.this.getTarget() != null) {
            SpellcastingIllagerEntity.this.getLookControl().lookAt(SpellcastingIllagerEntity.this.getTarget(), SpellcastingIllagerEntity.this.getMaxHeadRotation(), SpellcastingIllagerEntity.this.getMaxLookPitchChange());
        }
    }
}
