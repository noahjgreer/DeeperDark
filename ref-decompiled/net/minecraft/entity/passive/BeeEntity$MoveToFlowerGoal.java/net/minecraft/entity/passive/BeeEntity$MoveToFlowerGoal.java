/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;

public class BeeEntity.MoveToFlowerGoal
extends BeeEntity.NotAngryGoal {
    private static final int MAX_FLOWER_NAVIGATION_TICKS = 2400;
    int ticks;

    BeeEntity.MoveToFlowerGoal() {
        super(BeeEntity.this);
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canBeeStart() {
        return BeeEntity.this.flowerPos != null && !BeeEntity.this.hasPositionTarget() && this.shouldMoveToFlower() && !BeeEntity.this.isWithinDistance(BeeEntity.this.flowerPos, 2);
    }

    @Override
    public boolean canBeeContinue() {
        return this.canBeeStart();
    }

    @Override
    public void start() {
        this.ticks = 0;
        super.start();
    }

    @Override
    public void stop() {
        this.ticks = 0;
        BeeEntity.this.navigation.stop();
        BeeEntity.this.navigation.resetRangeMultiplier();
    }

    @Override
    public void tick() {
        if (BeeEntity.this.flowerPos == null) {
            return;
        }
        ++this.ticks;
        if (this.ticks > this.getTickCount(2400)) {
            BeeEntity.this.clearFlowerPos();
            return;
        }
        if (BeeEntity.this.navigation.isFollowingPath()) {
            return;
        }
        if (BeeEntity.this.isTooFar(BeeEntity.this.flowerPos)) {
            BeeEntity.this.clearFlowerPos();
            return;
        }
        BeeEntity.this.startMovingTo(BeeEntity.this.flowerPos);
    }

    private boolean shouldMoveToFlower() {
        return BeeEntity.this.ticksSincePollination > 600;
    }
}
