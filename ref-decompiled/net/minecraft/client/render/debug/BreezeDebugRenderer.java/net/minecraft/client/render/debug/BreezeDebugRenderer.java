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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class BreezeDebugRenderer
implements DebugRenderer.Renderer {
    private static final int PINK = ColorHelper.getArgb(255, 255, 100, 255);
    private static final int LIGHT_BLUE = ColorHelper.getArgb(255, 100, 255, 255);
    private static final int GREEN = ColorHelper.getArgb(255, 0, 255, 0);
    private static final int ORANGE = ColorHelper.getArgb(255, 255, 165, 0);
    private static final int RED = ColorHelper.getArgb(255, 255, 0, 0);
    private final MinecraftClient client;

    public BreezeDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        ClientWorld clientWorld = this.client.world;
        store.forEachEntityData(DebugSubscriptionTypes.BREEZES, (entity, data) -> {
            data.attackTarget().map(clientWorld::getEntityById).map(target -> target.getLerpedPos(this.client.getRenderTickCounter().getTickProgress(true))).ifPresent(targetPos -> {
                GizmoDrawing.arrow(entity.getEntityPos(), targetPos, LIGHT_BLUE);
                Vec3d vec3d = targetPos.add(0.0, 0.01f, 0.0);
                GizmoDrawing.circle(vec3d, 4.0f, DrawStyle.stroked(GREEN));
                GizmoDrawing.circle(vec3d, 8.0f, DrawStyle.stroked(ORANGE));
                GizmoDrawing.circle(vec3d, 24.0f, DrawStyle.stroked(RED));
            });
            data.jumpTarget().ifPresent(jumpTarget -> {
                GizmoDrawing.arrow(entity.getEntityPos(), jumpTarget.toCenterPos(), PINK);
                GizmoDrawing.box(Box.from(Vec3d.of(jumpTarget)), DrawStyle.filled(ColorHelper.fromFloats(1.0f, 1.0f, 0.0f, 0.0f)));
            });
        });
    }
}
