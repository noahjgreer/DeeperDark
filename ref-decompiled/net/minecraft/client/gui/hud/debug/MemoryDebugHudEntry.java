/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.hud.debug.DebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudLines
 *  net.minecraft.client.gui.hud.debug.MemoryDebugHudEntry
 *  net.minecraft.client.gui.hud.debug.MemoryDebugHudEntry$AllocationRateCalculator
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.WorldChunk
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.client.gui.hud.debug.MemoryDebugHudEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class MemoryDebugHudEntry
implements DebugHudEntry {
    private static final Identifier SECTION_ID = Identifier.ofVanilla((String)"memory");
    private final AllocationRateCalculator allocationRateCalculator = new AllocationRateCalculator();

    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        long l = Runtime.getRuntime().maxMemory();
        long m = Runtime.getRuntime().totalMemory();
        long n = Runtime.getRuntime().freeMemory();
        long o = m - n;
        lines.addLinesToSection(SECTION_ID, List.of(String.format(Locale.ROOT, "Mem: %2d%% %03d/%03dMB", o * 100L / l, MemoryDebugHudEntry.toMegabytes((long)o), MemoryDebugHudEntry.toMegabytes((long)l)), String.format(Locale.ROOT, "Allocation rate: %03dMB/s", MemoryDebugHudEntry.toMegabytes((long)this.allocationRateCalculator.get(o))), String.format(Locale.ROOT, "Allocated: %2d%% %03dMB", m * 100L / l, MemoryDebugHudEntry.toMegabytes((long)m))));
    }

    private static long toMegabytes(long bytes) {
        return bytes / 1024L / 1024L;
    }

    public boolean canShow(boolean reducedDebugInfo) {
        return true;
    }
}

