/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.ChunkBorderDebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class ChunkBorderDebugRenderer
implements DebugRenderer.Renderer {
    private static final float field_63585 = 4.0f;
    private static final float field_63586 = 1.0f;
    private final MinecraftClient client;
    private static final int DARK_CYAN = ColorHelper.getArgb((int)255, (int)0, (int)155, (int)155);
    private static final int YELLOW = ColorHelper.getArgb((int)255, (int)255, (int)255, (int)0);
    private static final int LIGHT_RED = ColorHelper.fromFloats((float)1.0f, (float)0.25f, (float)0.25f, (float)1.0f);

    public ChunkBorderDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        int j;
        int i;
        Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
        float f = this.client.world.getBottomY();
        float g = this.client.world.getTopYInclusive() + 1;
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from((BlockPos)entity.getBlockPos());
        double d = chunkSectionPos.getMinX();
        double e = chunkSectionPos.getMinZ();
        for (i = -16; i <= 32; i += 16) {
            for (j = -16; j <= 32; j += 16) {
                GizmoDrawing.line((Vec3d)new Vec3d(d + (double)i, (double)f, e + (double)j), (Vec3d)new Vec3d(d + (double)i, (double)g, e + (double)j), (int)ColorHelper.fromFloats((float)0.5f, (float)1.0f, (float)0.0f, (float)0.0f), (float)4.0f);
            }
        }
        for (i = 2; i < 16; i += 2) {
            j = i % 4 == 0 ? DARK_CYAN : YELLOW;
            GizmoDrawing.line((Vec3d)new Vec3d(d + (double)i, (double)f, e), (Vec3d)new Vec3d(d + (double)i, (double)g, e), (int)j, (float)1.0f);
            GizmoDrawing.line((Vec3d)new Vec3d(d + (double)i, (double)f, e + 16.0), (Vec3d)new Vec3d(d + (double)i, (double)g, e + 16.0), (int)j, (float)1.0f);
        }
        for (i = 2; i < 16; i += 2) {
            j = i % 4 == 0 ? DARK_CYAN : YELLOW;
            GizmoDrawing.line((Vec3d)new Vec3d(d, (double)f, e + (double)i), (Vec3d)new Vec3d(d, (double)g, e + (double)i), (int)j, (float)1.0f);
            GizmoDrawing.line((Vec3d)new Vec3d(d + 16.0, (double)f, e + (double)i), (Vec3d)new Vec3d(d + 16.0, (double)g, e + (double)i), (int)j, (float)1.0f);
        }
        for (i = this.client.world.getBottomY(); i <= this.client.world.getTopYInclusive() + 1; i += 2) {
            float h = i;
            int k = i % 8 == 0 ? DARK_CYAN : YELLOW;
            GizmoDrawing.line((Vec3d)new Vec3d(d, (double)h, e), (Vec3d)new Vec3d(d, (double)h, e + 16.0), (int)k, (float)1.0f);
            GizmoDrawing.line((Vec3d)new Vec3d(d, (double)h, e + 16.0), (Vec3d)new Vec3d(d + 16.0, (double)h, e + 16.0), (int)k, (float)1.0f);
            GizmoDrawing.line((Vec3d)new Vec3d(d + 16.0, (double)h, e + 16.0), (Vec3d)new Vec3d(d + 16.0, (double)h, e), (int)k, (float)1.0f);
            GizmoDrawing.line((Vec3d)new Vec3d(d + 16.0, (double)h, e), (Vec3d)new Vec3d(d, (double)h, e), (int)k, (float)1.0f);
        }
        for (i = 0; i <= 16; i += 16) {
            for (int j2 = 0; j2 <= 16; j2 += 16) {
                GizmoDrawing.line((Vec3d)new Vec3d(d + (double)i, (double)f, e + (double)j2), (Vec3d)new Vec3d(d + (double)i, (double)g, e + (double)j2), (int)LIGHT_RED, (float)4.0f);
            }
        }
        GizmoDrawing.box((Box)new Box((double)chunkSectionPos.getMinX(), (double)chunkSectionPos.getMinY(), (double)chunkSectionPos.getMinZ(), (double)(chunkSectionPos.getMaxX() + 1), (double)(chunkSectionPos.getMaxY() + 1), (double)(chunkSectionPos.getMaxZ() + 1)), (DrawStyle)DrawStyle.stroked((int)LIGHT_RED, (float)1.0f)).ignoreOcclusion();
        for (i = this.client.world.getBottomY(); i <= this.client.world.getTopYInclusive() + 1; i += 16) {
            GizmoDrawing.line((Vec3d)new Vec3d(d, (double)i, e), (Vec3d)new Vec3d(d, (double)i, e + 16.0), (int)LIGHT_RED, (float)4.0f);
            GizmoDrawing.line((Vec3d)new Vec3d(d, (double)i, e + 16.0), (Vec3d)new Vec3d(d + 16.0, (double)i, e + 16.0), (int)LIGHT_RED, (float)4.0f);
            GizmoDrawing.line((Vec3d)new Vec3d(d + 16.0, (double)i, e + 16.0), (Vec3d)new Vec3d(d + 16.0, (double)i, e), (int)LIGHT_RED, (float)4.0f);
            GizmoDrawing.line((Vec3d)new Vec3d(d + 16.0, (double)i, e), (Vec3d)new Vec3d(d, (double)i, e), (int)LIGHT_RED, (float)4.0f);
        }
    }
}

