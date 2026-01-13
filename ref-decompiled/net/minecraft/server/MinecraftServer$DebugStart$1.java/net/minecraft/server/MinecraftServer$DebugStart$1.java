/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;

class MinecraftServer.DebugStart.1
implements ProfileResult {
    final /* synthetic */ long field_39215;
    final /* synthetic */ int field_39216;

    MinecraftServer.DebugStart.1() {
        this.field_39215 = l;
        this.field_39216 = i;
    }

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
        return DebugStart.this.time;
    }

    @Override
    public int getStartTick() {
        return DebugStart.this.tick;
    }

    @Override
    public long getEndTime() {
        return this.field_39215;
    }

    @Override
    public int getEndTick() {
        return this.field_39216;
    }

    @Override
    public String getRootTimings() {
        return "";
    }
}
