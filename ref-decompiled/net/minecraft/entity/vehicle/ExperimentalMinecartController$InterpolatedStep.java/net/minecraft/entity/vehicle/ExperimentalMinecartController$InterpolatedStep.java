/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.vehicle;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;

static final class ExperimentalMinecartController.InterpolatedStep
extends Record {
    final float partialTicksInStep;
    final ExperimentalMinecartController.Step currentStep;
    final ExperimentalMinecartController.Step previousStep;

    ExperimentalMinecartController.InterpolatedStep(float partialTicksInStep, ExperimentalMinecartController.Step currentStep, ExperimentalMinecartController.Step previousStep) {
        this.partialTicksInStep = partialTicksInStep;
        this.currentStep = currentStep;
        this.previousStep = previousStep;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ExperimentalMinecartController.InterpolatedStep.class, "partialTicksInStep;currentStep;previousStep", "partialTicksInStep", "currentStep", "previousStep"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ExperimentalMinecartController.InterpolatedStep.class, "partialTicksInStep;currentStep;previousStep", "partialTicksInStep", "currentStep", "previousStep"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ExperimentalMinecartController.InterpolatedStep.class, "partialTicksInStep;currentStep;previousStep", "partialTicksInStep", "currentStep", "previousStep"}, this, object);
    }

    public float partialTicksInStep() {
        return this.partialTicksInStep;
    }

    public ExperimentalMinecartController.Step currentStep() {
        return this.currentStep;
    }

    public ExperimentalMinecartController.Step previousStep() {
        return this.previousStep;
    }
}
