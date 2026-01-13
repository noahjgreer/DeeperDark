/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.util.math.random.Random;

static class FindPointOfInterestTask.RetryMarker {
    private static final int MIN_DELAY = 40;
    private static final int MAX_EXTRA_DELAY = 80;
    private static final int ATTEMPT_DURATION = 400;
    private final Random random;
    private long previousAttemptAt;
    private long nextScheduledAttemptAt;
    private int currentDelay;

    FindPointOfInterestTask.RetryMarker(Random random, long time) {
        this.random = random;
        this.setAttemptTime(time);
    }

    public void setAttemptTime(long time) {
        this.previousAttemptAt = time;
        int i = this.currentDelay + this.random.nextInt(40) + 40;
        this.currentDelay = Math.min(i, 400);
        this.nextScheduledAttemptAt = time + (long)this.currentDelay;
    }

    public boolean isAttempting(long time) {
        return time - this.previousAttemptAt < 400L;
    }

    public boolean shouldRetry(long time) {
        return time >= this.nextScheduledAttemptAt;
    }

    public String toString() {
        return "RetryMarker{, previousAttemptAt=" + this.previousAttemptAt + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptAt + ", currentDelay=" + this.currentDelay + "}";
    }
}
