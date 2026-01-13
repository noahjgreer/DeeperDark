/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.hud.debug.DebugHudEntry
 *  net.minecraft.client.gui.hud.debug.DebugHudEntryCategory
 *  net.minecraft.client.gui.hud.debug.DebugHudLines
 *  net.minecraft.client.gui.hud.debug.RendererDebugHudEntry
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.WorldChunk
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryCategory;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RendererDebugHudEntry
implements DebugHudEntry {
    private final boolean ignoreReducedDebugInfo;

    public RendererDebugHudEntry() {
        this(false);
    }

    public RendererDebugHudEntry(boolean ignoreReducedDebugInfo) {
        this.ignoreReducedDebugInfo = ignoreReducedDebugInfo;
    }

    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
    }

    public boolean canShow(boolean reducedDebugInfo) {
        return this.ignoreReducedDebugInfo || !reducedDebugInfo;
    }

    public DebugHudEntryCategory getCategory() {
        return DebugHudEntryCategory.RENDERER;
    }
}

