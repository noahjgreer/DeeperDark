/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.ai.control.LookControl;

public class FoxEntity.FoxLookControl
extends LookControl {
    public FoxEntity.FoxLookControl() {
        super(FoxEntity.this);
    }

    @Override
    public void tick() {
        if (!FoxEntity.this.isSleeping()) {
            super.tick();
        }
    }

    @Override
    protected boolean shouldStayHorizontal() {
        return !FoxEntity.this.isChasing() && !FoxEntity.this.isInSneakingPose() && !FoxEntity.this.isRollingHead() && !FoxEntity.this.isWalking();
    }
}
