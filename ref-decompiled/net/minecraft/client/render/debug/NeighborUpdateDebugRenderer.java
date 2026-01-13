/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.NeighborUpdateDebugRenderer
 *  net.minecraft.client.render.debug.NeighborUpdateDebugRenderer$Update
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 */
package net.minecraft.client.render.debug;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.NeighborUpdateDebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class NeighborUpdateDebugRenderer
implements DebugRenderer.Renderer {
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        Update update;
        BlockPos blockPos;
        int i = DebugSubscriptionTypes.NEIGHBOR_UPDATES.getExpiry();
        double d = 1.0 / (double)(i * 2);
        HashMap map = new HashMap();
        store.forEachEvent(DebugSubscriptionTypes.NEIGHBOR_UPDATES, (pos, remainingTime, expiry) -> {
            long l = expiry - remainingTime;
            Update update = map.getOrDefault(pos, Update.EMPTY);
            map.put(pos, update.withAge((int)l));
        });
        for (Map.Entry entry : map.entrySet()) {
            blockPos = (BlockPos)entry.getKey();
            update = (Update)entry.getValue();
            Box box = new Box(blockPos).expand(0.002).contract(d * (double)update.age);
            GizmoDrawing.box((Box)box, (DrawStyle)DrawStyle.stroked((int)-1));
        }
        for (Map.Entry entry : map.entrySet()) {
            blockPos = (BlockPos)entry.getKey();
            update = (Update)entry.getValue();
            GizmoDrawing.text((String)String.valueOf(update.count), (Vec3d)Vec3d.ofCenter((Vec3i)blockPos), (TextGizmo.Style)TextGizmo.Style.left());
        }
    }
}

