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
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class EntityBlockIntersectionsDebugRenderer
implements DebugRenderer.Renderer {
    private static final float EXPANSION = 0.02f;

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        store.forEachBlockData(DebugSubscriptionTypes.ENTITY_BLOCK_INTERSECTIONS, (pos, type) -> GizmoDrawing.box(pos, 0.02f, DrawStyle.filled(type.getColor())));
    }
}
