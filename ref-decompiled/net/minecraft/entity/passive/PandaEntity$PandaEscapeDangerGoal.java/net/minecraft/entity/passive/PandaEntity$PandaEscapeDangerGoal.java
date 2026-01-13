/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.registry.tag.DamageTypeTags;

static class PandaEntity.PandaEscapeDangerGoal
extends EscapeDangerGoal {
    private final PandaEntity panda;

    public PandaEntity.PandaEscapeDangerGoal(PandaEntity panda, double speed) {
        super((PathAwareEntity)panda, speed, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES);
        this.panda = panda;
    }

    @Override
    public boolean shouldContinue() {
        if (this.panda.isSitting()) {
            this.panda.getNavigation().stop();
            return false;
        }
        return super.shouldContinue();
    }
}
