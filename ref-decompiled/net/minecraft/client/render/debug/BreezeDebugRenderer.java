/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.BreezeDebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class BreezeDebugRenderer
implements DebugRenderer.Renderer {
    private static final int PINK = ColorHelper.getArgb((int)255, (int)255, (int)100, (int)255);
    private static final int LIGHT_BLUE = ColorHelper.getArgb((int)255, (int)100, (int)255, (int)255);
    private static final int GREEN = ColorHelper.getArgb((int)255, (int)0, (int)255, (int)0);
    private static final int ORANGE = ColorHelper.getArgb((int)255, (int)255, (int)165, (int)0);
    private static final int RED = ColorHelper.getArgb((int)255, (int)255, (int)0, (int)0);
    private final MinecraftClient client;

    public BreezeDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        ClientWorld clientWorld = this.client.world;
        store.forEachEntityData(DebugSubscriptionTypes.BREEZES, (entity, data) -> {
            data.attackTarget().map(arg_0 -> ((ClientWorld)clientWorld).getEntityById(arg_0)).map(target -> target.getLerpedPos(this.client.getRenderTickCounter().getTickProgress(true))).ifPresent(targetPos -> {
                GizmoDrawing.arrow((Vec3d)entity.getEntityPos(), (Vec3d)targetPos, (int)LIGHT_BLUE);
                Vec3d vec3d = targetPos.add(0.0, (double)0.01f, 0.0);
                GizmoDrawing.circle((Vec3d)vec3d, (float)4.0f, (DrawStyle)DrawStyle.stroked((int)GREEN));
                GizmoDrawing.circle((Vec3d)vec3d, (float)8.0f, (DrawStyle)DrawStyle.stroked((int)ORANGE));
                GizmoDrawing.circle((Vec3d)vec3d, (float)24.0f, (DrawStyle)DrawStyle.stroked((int)RED));
            });
            data.jumpTarget().ifPresent(jumpTarget -> {
                GizmoDrawing.arrow((Vec3d)entity.getEntityPos(), (Vec3d)jumpTarget.toCenterPos(), (int)PINK);
                GizmoDrawing.box((Box)Box.from((Vec3d)Vec3d.of((Vec3i)jumpTarget)), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)1.0f, (float)1.0f, (float)0.0f, (float)0.0f)));
            });
        });
    }
}

