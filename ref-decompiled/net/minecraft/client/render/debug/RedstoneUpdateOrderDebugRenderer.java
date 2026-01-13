/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.RedstoneUpdateOrderDebugRenderer
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class RedstoneUpdateOrderDebugRenderer
implements DebugRenderer.Renderer {
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        store.forEachBlockData(DebugSubscriptionTypes.REDSTONE_WIRE_ORIENTATIONS, (blockPos, orientation) -> {
            Vec3d vec3d = blockPos.toBottomCenterPos().subtract(0.0, 0.1, 0.0);
            GizmoDrawing.arrow((Vec3d)vec3d, (Vec3d)vec3d.add(orientation.getFront().getDoubleVector().multiply(0.5)), (int)-16776961);
            GizmoDrawing.arrow((Vec3d)vec3d, (Vec3d)vec3d.add(orientation.getUp().getDoubleVector().multiply(0.4)), (int)-65536);
            GizmoDrawing.arrow((Vec3d)vec3d, (Vec3d)vec3d.add(orientation.getRight().getDoubleVector().multiply(0.3)), (int)-256);
        });
    }
}

