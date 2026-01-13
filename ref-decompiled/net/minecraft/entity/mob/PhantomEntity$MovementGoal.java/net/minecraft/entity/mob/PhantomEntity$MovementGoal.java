/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;

abstract class PhantomEntity.MovementGoal
extends Goal {
    public PhantomEntity.MovementGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    protected boolean isNearTarget() {
        return PhantomEntity.this.targetPosition.squaredDistanceTo(PhantomEntity.this.getX(), PhantomEntity.this.getY(), PhantomEntity.this.getZ()) < 4.0;
    }
}
