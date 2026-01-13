/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.SwimGoal;

class HappyGhastEntity.HappyGhastSwimGoal
extends SwimGoal {
    public HappyGhastEntity.HappyGhastSwimGoal() {
        super(HappyGhastEntity.this);
    }

    @Override
    public boolean canStart() {
        return !HappyGhastEntity.this.isStill() && super.canStart();
    }
}
