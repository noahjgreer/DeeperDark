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
 *  net.minecraft.client.render.debug.HeightmapDebugRenderer
 *  net.minecraft.client.render.debug.HeightmapDebugRenderer$1
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.debug;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.HeightmapDebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class HeightmapDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private static final int CHUNK_RANGE = 2;
    private static final float BOX_HEIGHT = 0.09375f;

    public HeightmapDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        ClientWorld worldAccess = this.client.world;
        BlockPos blockPos = BlockPos.ofFloored((double)cameraX, (double)0.0, (double)cameraZ);
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                Chunk chunk = worldAccess.getChunk(blockPos.add(i * 16, 0, j * 16));
                for (Map.Entry entry : chunk.getHeightmaps()) {
                    Heightmap.Type type = (Heightmap.Type)entry.getKey();
                    ChunkPos chunkPos = chunk.getPos();
                    Vector3f vector3f = this.getColorForHeightmapType(type);
                    for (int k = 0; k < 16; ++k) {
                        for (int l = 0; l < 16; ++l) {
                            int m = ChunkSectionPos.getOffsetPos((int)chunkPos.x, (int)k);
                            int n = ChunkSectionPos.getOffsetPos((int)chunkPos.z, (int)l);
                            float f = (float)worldAccess.getTopY(type, m, n) + (float)type.ordinal() * 0.09375f;
                            GizmoDrawing.box((Box)new Box((double)((float)m + 0.25f), (double)f, (double)((float)n + 0.25f), (double)((float)m + 0.75f), (double)(f + 0.09375f), (double)((float)n + 0.75f)), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)1.0f, (float)vector3f.x(), (float)vector3f.y(), (float)vector3f.z())));
                        }
                    }
                }
            }
        }
    }

    private Vector3f getColorForHeightmapType(Heightmap.Type type) {
        return switch (1.field_23778[type.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> new Vector3f(1.0f, 1.0f, 0.0f);
            case 2 -> new Vector3f(1.0f, 0.0f, 1.0f);
            case 3 -> new Vector3f(0.0f, 0.7f, 0.0f);
            case 4 -> new Vector3f(0.0f, 0.0f, 0.5f);
            case 5 -> new Vector3f(0.0f, 0.3f, 0.3f);
            case 6 -> new Vector3f(0.0f, 0.5f, 0.5f);
        };
    }
}

