/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.GoToVillageGoal;

class FoxEntity.GoToVillageGoal
extends GoToVillageGoal {
    public FoxEntity.GoToVillageGoal(int unused, int searchRange) {
        super(FoxEntity.this, searchRange);
    }

    @Override
    public void start() {
        FoxEntity.this.stopActions();
        super.start();
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.canGoToVillage();
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && this.canGoToVillage();
    }

    private boolean canGoToVillage() {
        return !FoxEntity.this.isSleeping() && !FoxEntity.this.isSitting() && !FoxEntity.this.isAggressive() && FoxEntity.this.getTarget() == null;
    }
}
