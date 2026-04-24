package net.noahsarch.deeperdark.ported;

public final class LeavesBeGonePort {

    private LeavesBeGonePort() {
    }

    public static void register() {
        // Leaf decay acceleration is handled entirely via mixins:
        // LeavesBeGoneServerLevelMixin, LeavesBeGoneLevelChunkMixin, LeavesBeGoneLeavesBlockMixin
    }
}
