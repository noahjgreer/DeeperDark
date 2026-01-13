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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
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

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        this.forEachEventData(store, (pos, radius) -> {
            double d = (double)radius * 2.0;
            GizmoDrawing.box(Box.of(pos, d, d, d), DrawStyle.filled(ColorHelper.fromFloats(0.35f, 1.0f, 1.0f, 0.0f)));
        });
        this.forEachEventData(store, (pos, radius) -> GizmoDrawing.box(Box.of(pos, 0.5, 1.0, 0.5).offset(0.0, 0.5, 0.0), DrawStyle.filled(ColorHelper.fromFloats(0.35f, 1.0f, 1.0f, 0.0f))));
        this.forEachEventData(store, (pos, radius) -> {
            GizmoDrawing.text("Listener Origin", pos.add(0.0, 1.8, 0.0), TextGizmo.Style.left().scaled(0.4f));
            GizmoDrawing.text(BlockPos.ofFloored(pos).toString(), pos.add(0.0, 1.5, 0.0), TextGizmo.Style.left(-6959665).scaled(0.4f));
        });
        store.forEachEvent(DebugSubscriptionTypes.GAME_EVENTS, (data, remainingTime, expiry) -> {
            Vec3d vec3d = data.pos();
            double d = 0.4;
            Box box = Box.of(vec3d.add(0.0, 0.5, 0.0), 0.4, 0.9, 0.4);
            GizmoDrawing.box(box, DrawStyle.filled(ColorHelper.fromFloats(0.2f, 1.0f, 1.0f, 1.0f)));
            GizmoDrawing.text(data.event().getIdAsString(), vec3d.add(0.0, 0.85, 0.0), TextGizmo.Style.left(-7564911).scaled(0.12f));
        });
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    static interface EventConsumer {
        public void accept(Vec3d var1, int var2);
    }
}
