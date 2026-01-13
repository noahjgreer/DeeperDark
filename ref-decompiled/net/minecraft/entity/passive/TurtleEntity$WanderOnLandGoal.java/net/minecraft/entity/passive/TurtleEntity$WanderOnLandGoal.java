/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.passive.TurtleEntity;

static class TurtleEntity.WanderOnLandGoal
extends WanderAroundGoal {
    private final TurtleEntity turtle;

    TurtleEntity.WanderOnLandGoal(TurtleEntity turtle, double speed, int chance) {
        super(turtle, speed, chance);
        this.turtle = turtle;
    }

    @Override
    public boolean canStart() {
        if (!(this.mob.isTouchingWater() || this.turtle.landBound || this.turtle.hasEgg())) {
            return super.canStart();
        }
        return false;
    }
}
