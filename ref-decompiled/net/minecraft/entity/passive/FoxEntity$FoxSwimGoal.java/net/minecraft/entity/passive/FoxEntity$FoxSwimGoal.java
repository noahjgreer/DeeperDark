/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.registry.tag.FluidTags;

class FoxEntity.FoxSwimGoal
extends SwimGoal {
    public FoxEntity.FoxSwimGoal() {
        super(FoxEntity.this);
    }

    @Override
    public void start() {
        super.start();
        FoxEntity.this.stopActions();
    }

    @Override
    public boolean canStart() {
        return FoxEntity.this.isTouchingWater() && FoxEntity.this.getFluidHeight(FluidTags.WATER) > 0.25 || FoxEntity.this.isInLava();
    }
}
