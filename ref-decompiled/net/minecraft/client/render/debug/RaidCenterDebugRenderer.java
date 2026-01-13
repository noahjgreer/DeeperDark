/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.RaidCenterDebugRenderer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RaidCenterDebugRenderer
implements DebugRenderer.Renderer {
    private static final int RANGE = 160;
    private static final float DRAWN_STRING_SIZE = 0.64f;
    private final MinecraftClient client;

    public RaidCenterDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        BlockPos blockPos = this.getCamera().getBlockPos();
        store.forEachChunkData(DebugSubscriptionTypes.RAIDS, (chunkPos, positions) -> {
            for (BlockPos blockPos2 : positions) {
                if (!blockPos.isWithinDistance((Vec3i)blockPos2, 160.0)) continue;
                RaidCenterDebugRenderer.drawRaidCenter((BlockPos)blockPos2);
            }
        });
    }

    private static void drawRaidCenter(BlockPos pos) {
        GizmoDrawing.box((BlockPos)pos, (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.15f, (float)1.0f, (float)0.0f, (float)0.0f)));
        RaidCenterDebugRenderer.drawString((String)"Raid center", (BlockPos)pos, (int)-65536);
    }

    private static void drawString(String text, BlockPos pos, int color) {
        GizmoDrawing.text((String)text, (Vec3d)Vec3d.add((Vec3i)pos, (double)0.5, (double)1.3, (double)0.5), (TextGizmo.Style)TextGizmo.Style.centered((int)color).scaled(0.64f)).ignoreOcclusion();
    }

    private Camera getCamera() {
        return this.client.gameRenderer.getCamera();
    }
}

