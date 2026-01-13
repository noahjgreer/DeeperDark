/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.ChunkLoadingDebugRenderer
 *  net.minecraft.client.render.debug.ChunkLoadingDebugRenderer$ChunkLoadingStatus
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.server.integrated.IntegratedServer
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.ChunkLoadingDebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
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
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ChunkLoadingStatus loadingData;

    public ChunkLoadingDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

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
            for (Map.Entry entry : this.loadingData.clientStates.entrySet()) {
                ChunkPos chunkPos = (ChunkPos)entry.getKey();
                Object string = (String)entry.getValue();
                if (map != null) {
                    string = (String)string + (String)map.get(chunkPos);
                }
                String[] strings = ((String)string).split("\n");
                int i = 0;
                for (String string2 : strings) {
                    GizmoDrawing.text((String)string2, (Vec3d)new Vec3d((double)ChunkSectionPos.getOffsetPos((int)chunkPos.x, (int)8), e + (double)i, (double)ChunkSectionPos.getOffsetPos((int)chunkPos.z, (int)8)), (TextGizmo.Style)TextGizmo.Style.left().scaled(2.4f)).ignoreOcclusion();
                    i -= 2;
                }
            }
        }
    }
}

