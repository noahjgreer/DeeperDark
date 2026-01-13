/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.ai.goal;

import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

public static class ChargeKineticWeaponGoal.Data {
    private int remainingUseTicks = -1;
    int chargeTicks = -1;
    @Nullable Vec3d startPos;
    boolean charged = false;

    public boolean isIdle() {
        return this.remainingUseTicks < 0;
    }

    public void setRemainingUseTicks(int remainingUseTicks) {
        this.remainingUseTicks = remainingUseTicks;
    }

    public boolean canStartCharging() {
        if (this.remainingUseTicks > 0) {
            --this.remainingUseTicks;
            if (this.remainingUseTicks == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean finishedCharging() {
        if (this.chargeTicks > 0) {
            ++this.chargeTicks;
            if ((double)this.chargeTicks > CHARGING_TIME_TICKS) {
                this.charged = true;
                return true;
            }
        }
        return false;
    }
}
