/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
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
    private static final int DARK_CYAN = ColorHelper.getArgb(255, 0, 155, 155);
    private static final int YELLOW = ColorHelper.getArgb(255, 255, 255, 0);
    private static final int LIGHT_RED = ColorHelper.fromFloats(1.0f, 0.25f, 0.25f, 1.0f);

    public ChunkBorderDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        int j;
        int i;
        Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
        float f = this.client.world.getBottomY();
        float g = this.client.world.getTopYInclusive() + 1;
        ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(entity.getBlockPos());
        double d = chunkSectionPos.getMinX();
        double e = chunkSectionPos.getMinZ();
        for (i = -16; i <= 32; i += 16) {
            for (j = -16; j <= 32; j += 16) {
                GizmoDrawing.line(new Vec3d(d + (double)i, f, e + (double)j), new Vec3d(d + (double)i, g, e + (double)j), ColorHelper.fromFloats(0.5f, 1.0f, 0.0f, 0.0f), 4.0f);
            }
        }
        for (i = 2; i < 16; i += 2) {
            j = i % 4 == 0 ? DARK_CYAN : YELLOW;
            GizmoDrawing.line(new Vec3d(d + (double)i, f, e), new Vec3d(d + (double)i, g, e), j, 1.0f);
            GizmoDrawing.line(new Vec3d(d + (double)i, f, e + 16.0), new Vec3d(d + (double)i, g, e + 16.0), j, 1.0f);
        }
        for (i = 2; i < 16; i += 2) {
            j = i % 4 == 0 ? DARK_CYAN : YELLOW;
            GizmoDrawing.line(new Vec3d(d, f, e + (double)i), new Vec3d(d, g, e + (double)i), j, 1.0f);
            GizmoDrawing.line(new Vec3d(d + 16.0, f, e + (double)i), new Vec3d(d + 16.0, g, e + (double)i), j, 1.0f);
        }
        for (i = this.client.world.getBottomY(); i <= this.client.world.getTopYInclusive() + 1; i += 2) {
            float h = i;
            int k = i % 8 == 0 ? DARK_CYAN : YELLOW;
            GizmoDrawing.line(new Vec3d(d, h, e), new Vec3d(d, h, e + 16.0), k, 1.0f);
            GizmoDrawing.line(new Vec3d(d, h, e + 16.0), new Vec3d(d + 16.0, h, e + 16.0), k, 1.0f);
            GizmoDrawing.line(new Vec3d(d + 16.0, h, e + 16.0), new Vec3d(d + 16.0, h, e), k, 1.0f);
            GizmoDrawing.line(new Vec3d(d + 16.0, h, e), new Vec3d(d, h, e), k, 1.0f);
        }
        for (i = 0; i <= 16; i += 16) {
            for (int j2 = 0; j2 <= 16; j2 += 16) {
                GizmoDrawing.line(new Vec3d(d + (double)i, f, e + (double)j2), new Vec3d(d + (double)i, g, e + (double)j2), LIGHT_RED, 4.0f);
            }
        }
        GizmoDrawing.box(new Box(chunkSectionPos.getMinX(), chunkSectionPos.getMinY(), chunkSectionPos.getMinZ(), chunkSectionPos.getMaxX() + 1, chunkSectionPos.getMaxY() + 1, chunkSectionPos.getMaxZ() + 1), DrawStyle.stroked(LIGHT_RED, 1.0f)).ignoreOcclusion();
        for (i = this.client.world.getBottomY(); i <= this.client.world.getTopYInclusive() + 1; i += 16) {
            GizmoDrawing.line(new Vec3d(d, i, e), new Vec3d(d, i, e + 16.0), LIGHT_RED, 4.0f);
            GizmoDrawing.line(new Vec3d(d, i, e + 16.0), new Vec3d(d + 16.0, i, e + 16.0), LIGHT_RED, 4.0f);
            GizmoDrawing.line(new Vec3d(d + 16.0, i, e + 16.0), new Vec3d(d + 16.0, i, e), LIGHT_RED, 4.0f);
            GizmoDrawing.line(new Vec3d(d + 16.0, i, e), new Vec3d(d, i, e), LIGHT_RED, 4.0f);
        }
    }
}
