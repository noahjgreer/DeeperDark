/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

class VexEntity.LookAtTargetGoal
extends Goal {
    public VexEntity.LookAtTargetGoal() {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return !VexEntity.this.getMoveControl().isMoving() && VexEntity.this.random.nextInt(VexEntity.LookAtTargetGoal.toGoalTicks(7)) == 0;
    }

    @Override
    public boolean shouldContinue() {
        return false;
    }

    @Override
    public void tick() {
        BlockPos blockPos = VexEntity.this.getBounds();
        if (blockPos == null) {
            blockPos = VexEntity.this.getBlockPos();
        }
        for (int i = 0; i < 3; ++i) {
            BlockPos blockPos2 = blockPos.add(VexEntity.this.random.nextInt(15) - 7, VexEntity.this.random.nextInt(11) - 5, VexEntity.this.random.nextInt(15) - 7);
            if (!VexEntity.this.getEntityWorld().isAir(blockPos2)) continue;
            VexEntity.this.moveControl.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
            if (VexEntity.this.getTarget() != null) break;
            VexEntity.this.getLookControl().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0f, 20.0f);
            break;
        }
    }
}
