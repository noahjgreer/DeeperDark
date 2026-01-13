/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

static class ExperimentalMinecartController.MoveIteration {
    double remainingMovement = 0.0;
    boolean initial = true;
    boolean slopeVelocityApplied = false;
    boolean decelerated = false;
    boolean accelerated = false;

    ExperimentalMinecartController.MoveIteration() {
    }

    public boolean shouldContinue() {
        return this.initial || this.remainingMovement > (double)1.0E-5f;
    }
}
