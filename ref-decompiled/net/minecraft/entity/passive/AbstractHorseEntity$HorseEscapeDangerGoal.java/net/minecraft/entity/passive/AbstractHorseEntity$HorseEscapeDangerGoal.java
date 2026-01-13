/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;

class AbstractHorseEntity.HorseEscapeDangerGoal
extends EscapeDangerGoal {
    public AbstractHorseEntity.HorseEscapeDangerGoal(double speed) {
        super(AbstractHorseEntity.this, speed);
    }

    @Override
    public boolean isInDanger() {
        return !AbstractHorseEntity.this.isControlledByMob() && super.isInDanger();
    }
}
