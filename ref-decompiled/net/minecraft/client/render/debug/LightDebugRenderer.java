/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.LightDebugRenderer
 *  net.minecraft.client.render.debug.LightDebugRenderer$Data
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.shape.VoxelSet
 *  net.minecraft.world.LightType
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.debug;

import java.time.Duration;
import java.time.Instant;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.LightDebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.LightType;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class LightDebugRenderer
implements DebugRenderer.Renderer {
    private static final Duration UPDATE_INTERVAL = Duration.ofMillis(500L);
    private static final int RADIUS = 10;
    private static final int READY_SHAPE_COLOR = ColorHelper.fromFloats((float)0.25f, (float)1.0f, (float)1.0f, (float)0.0f);
    private static final int DEFAULT_SHAPE_COLOR = ColorHelper.fromFloats((float)0.125f, (float)0.25f, (float)0.125f, (float)0.0f);
    private final MinecraftClient client;
    private final LightType lightType;
    private Instant lastUpdateTime = Instant.now();
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable LightDebugRenderer.Data data;

    public LightDebugRenderer(MinecraftClient client, LightType lightType) {
        this.client = client;
        this.lightType = lightType;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        Instant instant = Instant.now();
        if (this.data == null || Duration.between(this.lastUpdateTime, instant).compareTo(UPDATE_INTERVAL) > 0) {
            this.lastUpdateTime = instant;
            this.data = new Data(this.client.world.getLightingProvider(), ChunkSectionPos.from((BlockPos)this.client.player.getBlockPos()), 10, this.lightType);
        }
        LightDebugRenderer.drawEdges((VoxelSet)this.data.readyShape, (ChunkSectionPos)this.data.minSectionPos, (int)READY_SHAPE_COLOR);
        LightDebugRenderer.drawEdges((VoxelSet)this.data.shape, (ChunkSectionPos)this.data.minSectionPos, (int)DEFAULT_SHAPE_COLOR);
        LightDebugRenderer.drawFaces((VoxelSet)this.data.readyShape, (ChunkSectionPos)this.data.minSectionPos, (int)READY_SHAPE_COLOR);
        LightDebugRenderer.drawFaces((VoxelSet)this.data.shape, (ChunkSectionPos)this.data.minSectionPos, (int)DEFAULT_SHAPE_COLOR);
    }

    private static void drawFaces(VoxelSet voxelSet, ChunkSectionPos chunkSectionPos, int i) {
        voxelSet.forEachDirection((direction, j, k, l) -> {
            int m = j + chunkSectionPos.getX();
            int n = k + chunkSectionPos.getY();
            int o = l + chunkSectionPos.getZ();
            LightDebugRenderer.drawFace((Direction)direction, (int)m, (int)n, (int)o, (int)i);
        });
    }

    private static void drawEdges(VoxelSet voxelSet, ChunkSectionPos chunkSectionPos, int i) {
        voxelSet.forEachEdge((j, k, l, m, n, o) -> {
            int p = j + chunkSectionPos.getX();
            int q = k + chunkSectionPos.getY();
            int r = l + chunkSectionPos.getZ();
            int s = m + chunkSectionPos.getX();
            int t = n + chunkSectionPos.getY();
            int u = o + chunkSectionPos.getZ();
            LightDebugRenderer.drawEdge((int)p, (int)q, (int)r, (int)s, (int)t, (int)u, (int)i);
        }, true);
    }

    private static void drawFace(Direction direction, int i, int j, int k, int l) {
        Vec3d vec3d = new Vec3d((double)ChunkSectionPos.getBlockCoord((int)i), (double)ChunkSectionPos.getBlockCoord((int)j), (double)ChunkSectionPos.getBlockCoord((int)k));
        Vec3d vec3d2 = vec3d.add(16.0, 16.0, 16.0);
        GizmoDrawing.face((Vec3d)vec3d, (Vec3d)vec3d2, (Direction)direction, (DrawStyle)DrawStyle.filled((int)l));
    }

    private static void drawEdge(int i, int j, int k, int l, int m, int n, int o) {
        double d = ChunkSectionPos.getBlockCoord((int)i);
        double e = ChunkSectionPos.getBlockCoord((int)j);
        double f = ChunkSectionPos.getBlockCoord((int)k);
        double g = ChunkSectionPos.getBlockCoord((int)l);
        double h = ChunkSectionPos.getBlockCoord((int)m);
        double p = ChunkSectionPos.getBlockCoord((int)n);
        int q = ColorHelper.fullAlpha((int)o);
        GizmoDrawing.line((Vec3d)new Vec3d(d, e, f), (Vec3d)new Vec3d(g, h, p), (int)q);
    }
}

