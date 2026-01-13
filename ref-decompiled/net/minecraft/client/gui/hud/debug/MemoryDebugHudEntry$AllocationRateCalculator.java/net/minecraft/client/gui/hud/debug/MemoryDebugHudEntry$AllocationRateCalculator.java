/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.debug;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static class MemoryDebugHudEntry.AllocationRateCalculator {
    private static final int INTERVAL = 500;
    private static final List<GarbageCollectorMXBean> GARBAGE_COLLECTORS = ManagementFactory.getGarbageCollectorMXBeans();
    private long lastCalculated = 0L;
    private long allocatedBytes = -1L;
    private long collectionCount = -1L;
    private long allocationRate = 0L;

    MemoryDebugHudEntry.AllocationRateCalculator() {
    }

    long get(long allocatedBytes) {
        long l = System.currentTimeMillis();
        if (l - this.lastCalculated < 500L) {
            return this.allocationRate;
        }
        long m = MemoryDebugHudEntry.AllocationRateCalculator.getCollectionCount();
        if (this.lastCalculated != 0L && m == this.collectionCount) {
            double d = (double)TimeUnit.SECONDS.toMillis(1L) / (double)(l - this.lastCalculated);
            long n = allocatedBytes - this.allocatedBytes;
            this.allocationRate = Math.round((double)n * d);
        }
        this.lastCalculated = l;
        this.allocatedBytes = allocatedBytes;
        this.collectionCount = m;
        return this.allocationRate;
    }

    private static long getCollectionCount() {
        long l = 0L;
        for (GarbageCollectorMXBean garbageCollectorMXBean : GARBAGE_COLLECTORS) {
            l += garbageCollectorMXBean.getCollectionCount();
        }
        return l;
    }
}
