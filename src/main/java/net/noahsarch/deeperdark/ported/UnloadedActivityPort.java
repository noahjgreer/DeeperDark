package net.noahsarch.deeperdark.ported;

public final class UnloadedActivityPort {

    private UnloadedActivityPort() {
    }

    public static void register() {
        // Unloaded activity catch-up simulation is handled entirely via mixins:
        // UnloadedActivityChunkAccessMixin, UnloadedActivityLevelChunkMixin,
        // UnloadedActivitySerializableChunkDataMixin, UnloadedActivityServerLevelMixin
    }
}
