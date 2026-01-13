/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.debug.DebugHudEntryCategory;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface DebugHudEntry {
    public void render(DebugHudLines var1, @Nullable World var2, @Nullable WorldChunk var3, @Nullable WorldChunk var4);

    default public boolean canShow(boolean reducedDebugInfo) {
        return !reducedDebugInfo;
    }

    default public DebugHudEntryCategory getCategory() {
        return DebugHudEntryCategory.TEXT;
    }
}
