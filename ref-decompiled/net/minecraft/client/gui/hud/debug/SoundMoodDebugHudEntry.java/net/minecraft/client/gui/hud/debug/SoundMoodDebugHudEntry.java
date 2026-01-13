/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SoundMoodDebugHudEntry
implements DebugHudEntry {
    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player == null) {
            return;
        }
        lines.addLine(minecraftClient.getSoundManager().getDebugString() + String.format(Locale.ROOT, " (Mood %d%%)", Math.round(minecraftClient.player.getMoodPercentage() * 100.0f)));
    }
}
