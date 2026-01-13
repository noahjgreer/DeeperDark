/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.CollisionDebugRenderer
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(value=EnvType.CLIENT)
public class CollisionDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private double lastUpdateTime = Double.MIN_VALUE;
    private List<VoxelShape> collisions = Collections.emptyList();

    public CollisionDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        double d = Util.getMeasuringTimeNano();
        if (d - this.lastUpdateTime > 1.0E8) {
            this.lastUpdateTime = d;
            Entity entity = this.client.gameRenderer.getCamera().getFocusedEntity();
            this.collisions = ImmutableList.copyOf((Iterable)entity.getEntityWorld().getCollisions(entity, entity.getBoundingBox().expand(6.0)));
        }
        for (VoxelShape voxelShape : this.collisions) {
            DrawStyle drawStyle = DrawStyle.stroked((int)-1);
            for (Box box : voxelShape.getBoundingBoxes()) {
                GizmoDrawing.box((Box)box, (DrawStyle)drawStyle);
            }
        }
    }
}

