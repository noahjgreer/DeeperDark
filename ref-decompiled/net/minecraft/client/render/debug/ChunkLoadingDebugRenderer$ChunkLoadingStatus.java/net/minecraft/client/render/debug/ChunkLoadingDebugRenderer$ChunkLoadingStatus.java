/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.debug.ChunkLoadingDebugRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Environment(value=EnvType.CLIENT)
final class ChunkLoadingDebugRenderer.ChunkLoadingStatus {
    final Map<ChunkPos, String> clientStates;
    final CompletableFuture<Map<ChunkPos, String>> serverStates;

    ChunkLoadingDebugRenderer.ChunkLoadingStatus(ChunkLoadingDebugRenderer chunkLoadingDebugRenderer, IntegratedServer server, double x, double z) {
        ClientWorld clientWorld = chunkLoadingDebugRenderer.client.world;
        RegistryKey<World> registryKey = clientWorld.getRegistryKey();
        int i = ChunkSectionPos.getSectionCoord(x);
        int j = ChunkSectionPos.getSectionCoord(z);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        ClientChunkManager clientChunkManager = clientWorld.getChunkManager();
        for (int k = i - 12; k <= i + 12; ++k) {
            for (int l = j - 12; l <= j + 12; ++l) {
                ChunkPos chunkPos = new ChunkPos(k, l);
                Object string = "";
                WorldChunk worldChunk = clientChunkManager.getWorldChunk(k, l, false);
                string = (String)string + "Client: ";
                if (worldChunk == null) {
                    string = (String)string + "0n/a\n";
                } else {
                    string = (String)string + (worldChunk.isEmpty() ? " E" : "");
                    string = (String)string + "\n";
                }
                builder.put((Object)chunkPos, string);
            }
        }
        this.clientStates = builder.build();
        this.serverStates = server.submit(() -> {
            ServerWorld serverWorld = server.getWorld(registryKey);
            if (serverWorld == null) {
                return ImmutableMap.of();
            }
            ImmutableMap.Builder builder = ImmutableMap.builder();
            ServerChunkManager serverChunkManager = serverWorld.getChunkManager();
            for (int k = i - 12; k <= i + 12; ++k) {
                for (int l = j - 12; l <= j + 12; ++l) {
                    ChunkPos chunkPos = new ChunkPos(k, l);
                    builder.put((Object)chunkPos, (Object)("Server: " + serverChunkManager.getChunkLoadingDebugInfo(chunkPos)));
                }
            }
            return builder.build();
        });
    }
}
