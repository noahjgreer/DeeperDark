/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.ai.goal.Goal;

class ShulkerEntity.PeekGoal
extends Goal {
    private int counter;

    ShulkerEntity.PeekGoal() {
    }

    @Override
    public boolean canStart() {
        return ShulkerEntity.this.getTarget() == null && ShulkerEntity.this.random.nextInt(ShulkerEntity.PeekGoal.toGoalTicks(40)) == 0 && ShulkerEntity.this.canStay(ShulkerEntity.this.getBlockPos(), ShulkerEntity.this.getAttachedFace());
    }

    @Override
    public boolean shouldContinue() {
        return ShulkerEntity.this.getTarget() == null && this.counter > 0;
    }

    @Override
    public void start() {
        this.counter = this.getTickCount(20 * (1 + ShulkerEntity.this.random.nextInt(3)));
        ShulkerEntity.this.setPeekAmount(30);
    }

    @Override
    public void stop() {
        if (ShulkerEntity.this.getTarget() == null) {
            ShulkerEntity.this.setPeekAmount(0);
        }
    }

    @Override
    public void tick() {
        --this.counter;
    }
}
