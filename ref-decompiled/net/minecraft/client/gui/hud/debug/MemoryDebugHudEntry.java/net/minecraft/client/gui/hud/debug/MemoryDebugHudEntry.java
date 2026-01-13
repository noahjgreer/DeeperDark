/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class MemoryDebugHudEntry
implements DebugHudEntry {
    private static final Identifier SECTION_ID = Identifier.ofVanilla("memory");
    private final AllocationRateCalculator allocationRateCalculator = new AllocationRateCalculator();

    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        lines.addLinesToSection(SECTION_ID, List.of(String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", o * 100L / l, MemoryDebugHudEntry.toMegabytes(o), MemoryDebugHudEntry.toMegabytes(l)), String.format(Locale.ROOT, "Allocation rate: %03dMB/s", MemoryDebugHudEntry.toMegabytes(this.allocationRateCalculator.get(o))), String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", m * 100L / l, MemoryDebugHudEntry.toMegabytes(m))));
    }

    private static long toMegabytes(long bytes) {
        return bytes / 1024L / 1024L;
    }

    @Override
    public boolean canShow(boolean reducedDebugInfo) {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    static class AllocationRateCalculator {
        private static final int INTERVAL = 500;
        private static final List<GarbageCollectorMXBean> GARBAGE_COLLECTORS = ManagementFactory.getGarbageCollectorMXBeans();
        private long lastCalculated = 0L;
        private long allocatedBytes = -1L;
        private long collectionCount = -1L;
        private long allocationRate = 0L;

        AllocationRateCalculator() {
        }

        long get(long allocatedBytes) {
            long l = System.currentTimeMillis();
            if (l - this.lastCalculated < 500L) {
                return this.allocationRate;
            }
            long m = AllocationRateCalculator.getCollectionCount();
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
}
