/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        ClientWorld worldAccess = this.client.world;
        BlockPos blockPos = BlockPos.ofFloored(cameraX, 0.0, cameraZ);
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                Chunk chunk = worldAccess.getChunk(blockPos.add(i * 16, 0, j * 16));
                for (Map.Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
                    Heightmap.Type type = entry.getKey();
                    ChunkPos chunkPos = chunk.getPos();
                    Vector3f vector3f = this.getColorForHeightmapType(type);
                    for (int k = 0; k < 16; ++k) {
                        for (int l = 0; l < 16; ++l) {
                            int m = ChunkSectionPos.getOffsetPos(chunkPos.x, k);
                            int n = ChunkSectionPos.getOffsetPos(chunkPos.z, l);
                            float f = (float)worldAccess.getTopY(type, m, n) + (float)type.ordinal() * 0.09375f;
                            GizmoDrawing.box(new Box((float)m + 0.25f, f, (float)n + 0.25f, (float)m + 0.75f, f + 0.09375f, (float)n + 0.75f), DrawStyle.filled(ColorHelper.fromFloats(1.0f, vector3f.x(), vector3f.y(), vector3f.z())));
                        }
                    }
                }
            }
        }
    }

    private Vector3f getColorForHeightmapType(Heightmap.Type type) {
        return switch (type) {
            default -> throw new MatchException(null, null);
            case Heightmap.Type.WORLD_SURFACE_WG -> new Vector3f(1.0f, 1.0f, 0.0f);
            case Heightmap.Type.OCEAN_FLOOR_WG -> new Vector3f(1.0f, 0.0f, 1.0f);
            case Heightmap.Type.WORLD_SURFACE -> new Vector3f(0.0f, 0.7f, 0.0f);
            case Heightmap.Type.OCEAN_FLOOR -> new Vector3f(0.0f, 0.0f, 0.5f);
            case Heightmap.Type.MOTION_BLOCKING -> new Vector3f(0.0f, 0.3f, 0.3f);
            case Heightmap.Type.MOTION_BLOCKING_NO_LEAVES -> new Vector3f(0.0f, 0.5f, 0.5f);
        };
    }
}
