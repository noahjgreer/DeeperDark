/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.debug.DebugHudEntries
 *  net.minecraft.client.render.ChunkRenderingDataPreparer
 *  net.minecraft.client.render.ChunkRenderingDataPreparer$ChunkInfo
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk
 *  net.minecraft.client.render.debug.ChunkDebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  org.joml.Vector4f
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.render.ChunkRenderingDataPreparer;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.joml.Vector4f;

@Environment(value=EnvType.CLIENT)
public class ChunkDebugRenderer
implements DebugRenderer.Renderer {
    public static final Direction[] DIRECTIONS = Direction.values();
    private final MinecraftClient client;

    public ChunkDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        Frustum frustum2;
        WorldRenderer worldRenderer = this.client.worldRenderer;
        boolean bl = this.client.debugHudEntryList.isEntryVisible(DebugHudEntries.CHUNK_SECTION_PATHS);
        boolean bl2 = this.client.debugHudEntryList.isEntryVisible(DebugHudEntries.CHUNK_SECTION_VISIBILITY);
        if (bl || bl2) {
            ChunkRenderingDataPreparer chunkRenderingDataPreparer = worldRenderer.getChunkRenderingDataPreparer();
            for (ChunkBuilder.BuiltChunk builtChunk : worldRenderer.getBuiltChunks()) {
                int i;
                ChunkRenderingDataPreparer.ChunkInfo chunkInfo = chunkRenderingDataPreparer.getInfo(builtChunk);
                if (chunkInfo == null) continue;
                BlockPos blockPos = builtChunk.getOrigin();
                if (bl) {
                    i = chunkInfo.propagationLevel == 0 ? 0 : MathHelper.hsvToRgb((float)((float)chunkInfo.propagationLevel / 50.0f), (float)0.9f, (float)0.9f);
                    for (int j = 0; j < DIRECTIONS.length; ++j) {
                        if (!chunkInfo.hasDirection(j)) continue;
                        Direction direction = DIRECTIONS[j];
                        GizmoDrawing.line((Vec3d)Vec3d.add((Vec3i)blockPos, (double)8.0, (double)8.0, (double)8.0), (Vec3d)Vec3d.add((Vec3i)blockPos, (double)(8 - 16 * direction.getOffsetX()), (double)(8 - 16 * direction.getOffsetY()), (double)(8 - 16 * direction.getOffsetZ())), (int)ColorHelper.fullAlpha((int)i));
                    }
                }
                if (!bl2 || !builtChunk.getCurrentRenderData().hasData()) continue;
                i = 0;
                for (Direction direction2 : DIRECTIONS) {
                    for (Direction direction3 : DIRECTIONS) {
                        boolean bl3 = builtChunk.getCurrentRenderData().isVisibleThrough(direction2, direction3);
                        if (bl3) continue;
                        ++i;
                        GizmoDrawing.line((Vec3d)Vec3d.add((Vec3i)blockPos, (double)(8 + 8 * direction2.getOffsetX()), (double)(8 + 8 * direction2.getOffsetY()), (double)(8 + 8 * direction2.getOffsetZ())), (Vec3d)Vec3d.add((Vec3i)blockPos, (double)(8 + 8 * direction3.getOffsetX()), (double)(8 + 8 * direction3.getOffsetY()), (double)(8 + 8 * direction3.getOffsetZ())), (int)ColorHelper.getArgb((int)255, (int)255, (int)0, (int)0));
                    }
                }
                if (i <= 0) continue;
                float f = 0.5f;
                float g = 0.2f;
                GizmoDrawing.box((Box)builtChunk.getBoundingBox().contract(0.5), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.2f, (float)0.9f, (float)0.9f, (float)0.0f)));
            }
        }
        if ((frustum2 = worldRenderer.getCapturedFrustum()) != null) {
            Vec3d vec3d = new Vec3d(frustum2.getX(), frustum2.getY(), frustum2.getZ());
            Vector4f[] vector4fs = frustum2.getBoundaryPoints();
            this.addFace(vec3d, vector4fs, 0, 1, 2, 3, 0, 1, 1);
            this.addFace(vec3d, vector4fs, 4, 5, 6, 7, 1, 0, 0);
            this.addFace(vec3d, vector4fs, 0, 1, 5, 4, 1, 1, 0);
            this.addFace(vec3d, vector4fs, 2, 3, 7, 6, 0, 0, 1);
            this.addFace(vec3d, vector4fs, 0, 4, 7, 3, 0, 1, 0);
            this.addFace(vec3d, vector4fs, 1, 5, 6, 2, 1, 0, 1);
            this.addFrustumEdge(vec3d, vector4fs[0], vector4fs[1]);
            this.addFrustumEdge(vec3d, vector4fs[1], vector4fs[2]);
            this.addFrustumEdge(vec3d, vector4fs[2], vector4fs[3]);
            this.addFrustumEdge(vec3d, vector4fs[3], vector4fs[0]);
            this.addFrustumEdge(vec3d, vector4fs[4], vector4fs[5]);
            this.addFrustumEdge(vec3d, vector4fs[5], vector4fs[6]);
            this.addFrustumEdge(vec3d, vector4fs[6], vector4fs[7]);
            this.addFrustumEdge(vec3d, vector4fs[7], vector4fs[4]);
            this.addFrustumEdge(vec3d, vector4fs[0], vector4fs[4]);
            this.addFrustumEdge(vec3d, vector4fs[1], vector4fs[5]);
            this.addFrustumEdge(vec3d, vector4fs[2], vector4fs[6]);
            this.addFrustumEdge(vec3d, vector4fs[3], vector4fs[7]);
        }
    }

    private void addFrustumEdge(Vec3d origin, Vector4f startOffset, Vector4f endOffset) {
        GizmoDrawing.line((Vec3d)new Vec3d(origin.x + (double)startOffset.x, origin.y + (double)startOffset.y, origin.z + (double)startOffset.z), (Vec3d)new Vec3d(origin.x + (double)endOffset.x, origin.y + (double)endOffset.y, origin.z + (double)endOffset.z), (int)-16777216);
    }

    private void addFace(Vec3d origin, Vector4f[] vertexOffsets, int i1, int i2, int i3, int i4, int red, int green, int blue) {
        float f = 0.25f;
        GizmoDrawing.quad((Vec3d)new Vec3d((double)vertexOffsets[i1].x(), (double)vertexOffsets[i1].y(), (double)vertexOffsets[i1].z()).add(origin), (Vec3d)new Vec3d((double)vertexOffsets[i2].x(), (double)vertexOffsets[i2].y(), (double)vertexOffsets[i2].z()).add(origin), (Vec3d)new Vec3d((double)vertexOffsets[i3].x(), (double)vertexOffsets[i3].y(), (double)vertexOffsets[i3].z()).add(origin), (Vec3d)new Vec3d((double)vertexOffsets[i4].x(), (double)vertexOffsets[i4].y(), (double)vertexOffsets[i4].z()).add(origin), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.25f, (float)red, (float)green, (float)blue)));
    }
}

