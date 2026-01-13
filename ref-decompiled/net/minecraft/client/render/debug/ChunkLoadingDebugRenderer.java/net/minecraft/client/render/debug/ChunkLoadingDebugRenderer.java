/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChunkLoadingDebugRenderer
implements DebugRenderer.Renderer {
    final MinecraftClient client;
    private double lastUpdateTime = Double.MIN_VALUE;
    private final int LOADING_DATA_CHUNK_RANGE = 12;
    private @Nullable ChunkLoadingStatus loadingData;

    public ChunkLoadingDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        double d = Util.getMeasuringTimeNano();
        if (d - this.lastUpdateTime > 3.0E9) {
            this.lastUpdateTime = d;
            IntegratedServer integratedServer = this.client.getServer();
            this.loadingData = integratedServer != null ? new ChunkLoadingStatus(this, integratedServer, cameraX, cameraZ) : null;
        }
        if (this.loadingData != null) {
            Map map = this.loadingData.serverStates.getNow(null);
            double e = this.client.gameRenderer.getCamera().getCameraPos().y * 0.85;
            for (Map.Entry<ChunkPos, String> entry : this.loadingData.clientStates.entrySet()) {
                ChunkPos chunkPos = entry.getKey();
                Object string = entry.getValue();
                if (map != null) {
                    string = (String)string + (String)map.get(chunkPos);
                }
                String[] strings = ((String)string).split("\n");
                int i = 0;
                for (String string2 : strings) {
                    GizmoDrawing.text(string2, new Vec3d(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), e + (double)i, ChunkSectionPos.getOffsetPos(chunkPos.z, 8)), TextGizmo.Style.left().scaled(2.4f)).ignoreOcclusion();
                    i -= 2;
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    final class ChunkLoadingStatus {
        final Map<ChunkPos, String> clientStates;
        final CompletableFuture<Map<ChunkPos, String>> serverStates;

        ChunkLoadingStatus(ChunkLoadingDebugRenderer chunkLoadingDebugRenderer, IntegratedServer server, double x, double z) {
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
}
