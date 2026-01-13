/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;

class GoalSelector.2
extends PrioritizedGoal {
    GoalSelector.2(int i, Goal goal) {
        super(i, goal);
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
