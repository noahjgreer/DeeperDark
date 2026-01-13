/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud.debug;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EntitySpawnCountsDebugHudEntry
implements DebugHudEntry {
    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk clientChunk, @Nullable WorldChunk chunk) {
        ServerWorld serverWorld;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        Entity entity = minecraftClient.getCameraEntity();
        ServerWorld serverWorld2 = serverWorld = world instanceof ServerWorld ? (ServerWorld)world : null;
        if (entity == null || serverWorld == null) {
            return;
        }
        ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
        SpawnHelper.Info info = serverChunkManager.getSpawnInfo();
        if (info != null) {
            Object2IntMap<SpawnGroup> object2IntMap = info.getGroupToCount();
            int i = info.getSpawningChunkCount();
            lines.addLine("SC: " + i + ", " + Stream.of(SpawnGroup.values()).map(spawnGroup -> Character.toUpperCase(spawnGroup.getName().charAt(0)) + ": " + object2IntMap.getInt(spawnGroup)).collect(Collectors.joining(", ")));
        }
    }
}
