/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.Goal;

abstract class BeeEntity.NotAngryGoal
extends Goal {
    BeeEntity.NotAngryGoal() {
    }

    public abstract boolean canBeeStart();

    public abstract boolean canBeeContinue();

    @Override
    public boolean canStart() {
        return this.canBeeStart() && !BeeEntity.this.hasAngerTime();
    }

    @Override
    public boolean shouldContinue() {
        return this.canBeeContinue() && !BeeEntity.this.hasAngerTime();
    }
}
