/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;

static class TurtleEntity.TurtleEscapeDangerGoal
extends EscapeDangerGoal {
    TurtleEntity.TurtleEscapeDangerGoal(TurtleEntity turtle, double speed) {
        super(turtle, speed);
    }

    @Override
    public boolean canStart() {
        if (!this.isInDanger()) {
            return false;
        }
        BlockPos blockPos = this.locateClosestWater(this.mob.getEntityWorld(), this.mob, 7);
        if (blockPos != null) {
            this.targetX = blockPos.getX();
            this.targetY = blockPos.getY();
            this.targetZ = blockPos.getZ();
            return true;
        }
        return this.findTarget();
    }
}
