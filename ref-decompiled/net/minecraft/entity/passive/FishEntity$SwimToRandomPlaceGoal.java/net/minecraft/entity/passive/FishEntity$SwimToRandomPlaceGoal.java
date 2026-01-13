/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.passive.FishEntity;

static class FishEntity.SwimToRandomPlaceGoal
extends SwimAroundGoal {
    private final FishEntity fish;

    public FishEntity.SwimToRandomPlaceGoal(FishEntity fish) {
        super(fish, 1.0, 40);
        this.fish = fish;
    }

    @Override
    public boolean canStart() {
        return this.fish.hasSelfControl() && super.canStart();
    }
}
