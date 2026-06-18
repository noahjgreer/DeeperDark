package net.noahsarch.deeperdark.client;

/**
 * Tracks the last game tick on which the local player was inside a quicksand block.
 * Written from GuiQuicksandMixin each frame; read by GuiHeartTypeQuicksandMixin and
 * the overlay / fog mixins to suppress freeze-themed visuals.
 */
public final class QuicksandClientTracker {
    private QuicksandClientTracker() {}

    public static long lastInQuicksandTick = Long.MIN_VALUE;

    /** Freeze ticks drain at 2 per tick; max is ~140. Keep suppressing for a comfortable margin. */
    private static final long SUPPRESS_FOR_TICKS = 100L;

    public static boolean isActive(long currentGameTick) {
        return currentGameTick - lastInQuicksandTick < SUPPRESS_FOR_TICKS;
    }
}
