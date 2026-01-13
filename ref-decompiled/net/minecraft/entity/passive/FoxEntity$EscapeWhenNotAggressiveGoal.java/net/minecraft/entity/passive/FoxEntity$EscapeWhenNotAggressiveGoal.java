/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;

class FoxEntity.EscapeWhenNotAggressiveGoal
extends EscapeDangerGoal {
    public FoxEntity.EscapeWhenNotAggressiveGoal(double speed) {
        super(FoxEntity.this, speed);
    }

    @Override
    public boolean isInDanger() {
        return !FoxEntity.this.isAggressive() && super.isInDanger();
    }
}
