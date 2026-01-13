/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.DebugSubscriptionTypes;
import net.minecraft.world.debug.data.EntityPathDebugData;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;

@Environment(value=EnvType.CLIENT)
public class PathfindingDebugRenderer
implements DebugRenderer.Renderer {
    private static final float RANGE = 80.0f;
    private static final int field_62974 = 8;
    private static final boolean field_62975 = false;
    private static final boolean field_32908 = true;
    private static final boolean field_32909 = false;
    private static final boolean field_32910 = false;
    private static final boolean field_32911 = true;
    private static final boolean field_32912 = true;
    private static final float DRAWN_STRING_SIZE = 0.32f;

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        store.forEachEntityData(DebugSubscriptionTypes.ENTITY_PATHS, (entity, debugData) -> PathfindingDebugRenderer.render(cameraX, cameraY, cameraZ, debugData.path(), debugData.maxNodeDistance()));
    }

    private static void render(double cameraX, double cameraY, double cameraZ, Path path, float maxNodeDistance) {
        PathfindingDebugRenderer.drawPath(path, maxNodeDistance, true, true, cameraX, cameraY, cameraZ);
    }

    public static void drawPath(Path path, float maxNodeDistance, boolean bl, boolean bl2, double cameraX, double cameraY, double cameraZ) {
        PathfindingDebugRenderer.drawPathLines(path, cameraX, cameraY, cameraZ);
        BlockPos blockPos = path.getTarget();
        if (PathfindingDebugRenderer.getManhattanDistance(blockPos, cameraX, cameraY, cameraZ) <= 80.0f) {
            GizmoDrawing.box(new Box((float)blockPos.getX() + 0.25f, (float)blockPos.getY() + 0.25f, (double)blockPos.getZ() + 0.25, (float)blockPos.getX() + 0.75f, (float)blockPos.getY() + 0.75f, (float)blockPos.getZ() + 0.75f), DrawStyle.filled(ColorHelper.fromFloats(0.5f, 0.0f, 1.0f, 0.0f)));
            for (int i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                float f = i == path.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float g = i == path.getCurrentNodeIndex() ? 0.0f : 1.0f;
                Box box = new Box((float)pathNode.x + 0.5f - maxNodeDistance, (float)pathNode.y + 0.01f * (float)i, (float)pathNode.z + 0.5f - maxNodeDistance, (float)pathNode.x + 0.5f + maxNodeDistance, (float)pathNode.y + 0.25f + 0.01f * (float)i, (float)pathNode.z + 0.5f + maxNodeDistance);
                GizmoDrawing.box(box, DrawStyle.filled(ColorHelper.fromFloats(0.5f, f, 0.0f, g)));
            }
        }
        Path.DebugNodeInfo debugNodeInfo = path.getDebugNodeInfos();
        if (bl && debugNodeInfo != null) {
            for (PathNode pathNode2 : debugNodeInfo.closedSet()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode2.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                GizmoDrawing.box(new Box((float)pathNode2.x + 0.5f - maxNodeDistance / 2.0f, (float)pathNode2.y + 0.01f, (float)pathNode2.z + 0.5f - maxNodeDistance / 2.0f, (float)pathNode2.x + 0.5f + maxNodeDistance / 2.0f, (double)pathNode2.y + 0.1, (float)pathNode2.z + 0.5f + maxNodeDistance / 2.0f), DrawStyle.filled(ColorHelper.fromFloats(0.5f, 1.0f, 0.8f, 0.8f)));
            }
            for (PathNode pathNode2 : debugNodeInfo.openSet()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode2.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                GizmoDrawing.box(new Box((float)pathNode2.x + 0.5f - maxNodeDistance / 2.0f, (float)pathNode2.y + 0.01f, (float)pathNode2.z + 0.5f - maxNodeDistance / 2.0f, (float)pathNode2.x + 0.5f + maxNodeDistance / 2.0f, (double)pathNode2.y + 0.1, (float)pathNode2.z + 0.5f + maxNodeDistance / 2.0f), DrawStyle.filled(ColorHelper.fromFloats(0.5f, 0.8f, 1.0f, 1.0f)));
            }
        }
        if (bl2) {
            for (int j = 0; j < path.getLength(); ++j) {
                PathNode pathNode3 = path.getNode(j);
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode3.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                GizmoDrawing.text(String.valueOf((Object)pathNode3.type), new Vec3d((double)pathNode3.x + 0.5, (double)pathNode3.y + 0.75, (double)pathNode3.z + 0.5), TextGizmo.Style.left().scaled(0.32f)).ignoreOcclusion();
                GizmoDrawing.text(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode3.penalty)), new Vec3d((double)pathNode3.x + 0.5, (double)pathNode3.y + 0.25, (double)pathNode3.z + 0.5), TextGizmo.Style.left().scaled(0.32f)).ignoreOcclusion();
            }
        }
    }

    public static void drawPathLines(Path path, double cameraX, double cameraY, double cameraZ) {
        if (path.getLength() < 2) {
            return;
        }
        Vec3d vec3d = path.getNode(0).getPos();
        for (int i = 1; i < path.getLength(); ++i) {
            PathNode pathNode = path.getNode(i);
            if (PathfindingDebugRenderer.getManhattanDistance(pathNode.getBlockPos(), cameraX, cameraY, cameraZ) > 80.0f) {
                vec3d = pathNode.getPos();
                continue;
            }
            float f = (float)i / (float)path.getLength() * 0.33f;
            int j = ColorHelper.fullAlpha(MathHelper.hsvToRgb(f, 0.9f, 0.9f));
            GizmoDrawing.arrow(vec3d.add(0.5, 0.5, 0.5), pathNode.getPos().add(0.5, 0.5, 0.5), j);
            vec3d = pathNode.getPos();
        }
    }

    private static float getManhattanDistance(BlockPos pos, double x, double y, double z) {
        return (float)(Math.abs((double)pos.getX() - x) + Math.abs((double)pos.getY() - y) + Math.abs((double)pos.getZ() - z));
    }

    private static /* synthetic */ void method_75445(DebugDataStore debugDataStore, double d, double e, double f, Entity entity) {
        EntityPathDebugData entityPathDebugData = debugDataStore.getEntityData(DebugSubscriptionTypes.ENTITY_PATHS, entity);
        if (entityPathDebugData != null) {
            PathfindingDebugRenderer.render(d, e, f, entityPathDebugData.path(), entityPathDebugData.maxNodeDistance());
        }
    }
}
