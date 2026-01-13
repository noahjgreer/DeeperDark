/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.GameEventDebugRenderer
 *  net.minecraft.client.render.debug.GameEventDebugRenderer$EventConsumer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.GameEventDebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class GameEventDebugRenderer
implements DebugRenderer.Renderer {
    private static final float field_32900 = 1.0f;

    private void forEachEventData(DebugDataStore dataStore, EventConsumer consumer) {
        dataStore.forEachBlockData(DebugSubscriptionTypes.GAME_EVENT_LISTENERS, (pos, data) -> consumer.accept(pos.toCenterPos(), data.listenerRadius()));
        dataStore.forEachEntityData(DebugSubscriptionTypes.GAME_EVENT_LISTENERS, (entity, data) -> consumer.accept(entity.getEntityPos(), data.listenerRadius()));
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        this.forEachEventData(store, (pos, radius) -> {
            double d = (double)radius * 2.0;
            GizmoDrawing.box((Box)Box.of((Vec3d)pos, (double)d, (double)d, (double)d), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.35f, (float)1.0f, (float)1.0f, (float)0.0f)));
        });
        this.forEachEventData(store, (pos, radius) -> GizmoDrawing.box((Box)Box.of((Vec3d)pos, (double)0.5, (double)1.0, (double)0.5).offset(0.0, 0.5, 0.0), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.35f, (float)1.0f, (float)1.0f, (float)0.0f))));
        this.forEachEventData(store, (pos, radius) -> {
            GizmoDrawing.text((String)"Listener Origin", (Vec3d)pos.add(0.0, 1.8, 0.0), (TextGizmo.Style)TextGizmo.Style.left().scaled(0.4f));
            GizmoDrawing.text((String)BlockPos.ofFloored((Position)pos).toString(), (Vec3d)pos.add(0.0, 1.5, 0.0), (TextGizmo.Style)TextGizmo.Style.left((int)-6959665).scaled(0.4f));
        });
        store.forEachEvent(DebugSubscriptionTypes.GAME_EVENTS, (data, remainingTime, expiry) -> {
            Vec3d vec3d = data.pos();
            double d = 0.4;
            Box box = Box.of((Vec3d)vec3d.add(0.0, 0.5, 0.0), (double)0.4, (double)0.9, (double)0.4);
            GizmoDrawing.box((Box)box, (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.2f, (float)1.0f, (float)1.0f, (float)1.0f)));
            GizmoDrawing.text((String)data.event().getIdAsString(), (Vec3d)vec3d.add(0.0, 0.85, 0.0), (TextGizmo.Style)TextGizmo.Style.left((int)-7564911).scaled(0.12f));
        });
    }
}

