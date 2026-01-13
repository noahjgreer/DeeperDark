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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkSourceStatsDebugHudEntry
implements DebugHudEntry {
    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.world != null) {
            lines.addLine(minecraftClient.world.asString());
        }
        if (world != null && world != minecraftClient.world) {
            lines.addLine(world.asString());
        }
    }

    @Override
    public boolean canShow(boolean reducedDebugInfo) {
        return true;
    }
}
