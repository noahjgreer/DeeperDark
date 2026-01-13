/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.mob.SpellcastingIllagerEntity;

class EvokerEntity.LookAtTargetOrWololoTarget
extends SpellcastingIllagerEntity.LookAtTargetGoal {
    EvokerEntity.LookAtTargetOrWololoTarget() {
        super(EvokerEntity.this);
    }

    @Override
    public void tick() {
        if (EvokerEntity.this.getTarget() != null) {
            EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getTarget(), EvokerEntity.this.getMaxHeadRotation(), EvokerEntity.this.getMaxLookPitchChange());
        } else if (EvokerEntity.this.getWololoTarget() != null) {
            EvokerEntity.this.getLookControl().lookAt(EvokerEntity.this.getWololoTarget(), EvokerEntity.this.getMaxHeadRotation(), EvokerEntity.this.getMaxLookPitchChange());
        }
    }
}
