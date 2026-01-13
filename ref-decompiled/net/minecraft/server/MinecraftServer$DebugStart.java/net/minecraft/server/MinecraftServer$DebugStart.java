/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;

static class MinecraftServer.DebugStart {
    final long time;
    final int tick;

    MinecraftServer.DebugStart(long time, int tick) {
        this.time = time;
        this.tick = tick;
    }

    ProfileResult end(final long endTime, final int endTick) {
        return new ProfileResult(){

            @Override
            public List<ProfilerTiming> getTimings(String parentPath) {
                return Collections.emptyList();
            }

            @Override
            public boolean save(Path path) {
                return false;
            }

            @Override
            public long getStartTime() {
                return time;
            }

            @Override
            public int getStartTick() {
                return tick;
            }

            @Override
            public long getEndTime() {
                return endTime;
            }

            @Override
            public int getEndTick() {
                return endTick;
            }

            @Override
            public String getRootTimings() {
                return "";
            }
        };
    }
}
