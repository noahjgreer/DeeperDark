/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.player;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;

static final class ItemCooldownManager.Entry
extends Record {
    final int startTick;
    final int endTick;

    ItemCooldownManager.Entry(int startTick, int endTick) {
        this.startTick = startTick;
        this.endTick = endTick;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemCooldownManager.Entry.class, "startTime;endTime", "startTick", "endTick"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemCooldownManager.Entry.class, "startTime;endTime", "startTick", "endTick"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemCooldownManager.Entry.class, "startTime;endTime", "startTick", "endTick"}, this, object);
    }

    public int startTick() {
        return this.startTick;
    }

    public int endTick() {
        return this.endTick;
    }
}
