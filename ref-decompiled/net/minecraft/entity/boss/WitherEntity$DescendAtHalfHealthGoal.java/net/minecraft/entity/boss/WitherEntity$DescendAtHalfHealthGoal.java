/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.boss;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;

class WitherEntity.DescendAtHalfHealthGoal
extends Goal {
    public WitherEntity.DescendAtHalfHealthGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return WitherEntity.this.getInvulnerableTimer() > 0;
    }
}
