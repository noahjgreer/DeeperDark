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
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class RaidCenterDebugRenderer
implements DebugRenderer.Renderer {
    private static final int RANGE = 160;
    private static final float DRAWN_STRING_SIZE = 0.64f;
    private final MinecraftClient client;

    public RaidCenterDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        BlockPos blockPos = this.getCamera().getBlockPos();
        store.forEachChunkData(DebugSubscriptionTypes.RAIDS, (chunkPos, positions) -> {
            for (BlockPos blockPos2 : positions) {
                if (!blockPos.isWithinDistance(blockPos2, 160.0)) continue;
                RaidCenterDebugRenderer.drawRaidCenter(blockPos2);
            }
        });
    }

    private static void drawRaidCenter(BlockPos pos) {
        GizmoDrawing.box(pos, DrawStyle.filled(ColorHelper.fromFloats(0.15f, 1.0f, 0.0f, 0.0f)));
        RaidCenterDebugRenderer.drawString("Raid center", pos, -65536);
    }

    private static void drawString(String text, BlockPos pos, int color) {
        GizmoDrawing.text(text, Vec3d.add(pos, 0.5, 1.3, 0.5), TextGizmo.Style.centered(color).scaled(0.64f)).ignoreOcclusion();
    }

    private Camera getCamera() {
        return this.client.gameRenderer.getCamera();
    }
}
