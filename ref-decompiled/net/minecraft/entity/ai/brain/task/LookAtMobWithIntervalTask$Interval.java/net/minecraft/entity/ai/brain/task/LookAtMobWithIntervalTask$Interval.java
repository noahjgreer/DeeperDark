/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;

public static final class LookAtMobWithIntervalTask.Interval {
    private final UniformIntProvider interval;
    private int remainingTicks;

    public LookAtMobWithIntervalTask.Interval(UniformIntProvider interval) {
        if (interval.getMin() <= 1) {
            throw new IllegalArgumentException();
        }
        this.interval = interval;
    }

    public boolean shouldRun(Random random) {
        if (this.remainingTicks == 0) {
            this.remainingTicks = this.interval.get(random) - 1;
            return false;
        }
        return --this.remainingTicks == 0;
    }
}
