/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.boss.dragon;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;

public static final class EnderDragonFrameTracker.Frame
extends Record {
    final double y;
    final float yRot;

    public EnderDragonFrameTracker.Frame(double y, float yRot) {
        this.y = y;
        this.yRot = yRot;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EnderDragonFrameTracker.Frame.class, "y;yRot", "y", "yRot"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EnderDragonFrameTracker.Frame.class, "y;yRot", "y", "yRot"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EnderDragonFrameTracker.Frame.class, "y;yRot", "y", "yRot"}, this, object);
    }

    public double y() {
        return this.y;
    }

    public float yRot() {
        return this.yRot;
    }
}
