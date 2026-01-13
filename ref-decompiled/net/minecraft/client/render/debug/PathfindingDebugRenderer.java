/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.PathfindingDebugRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.pathing.Path
 *  net.minecraft.entity.ai.pathing.Path$DebugNodeInfo
 *  net.minecraft.entity.ai.pathing.PathNode
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.DebugSubscriptionTypes
 *  net.minecraft.world.debug.data.EntityPathDebugData
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
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

/*
 * Exception performing whole class analysis ignored.
 */
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

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        store.forEachEntityData(DebugSubscriptionTypes.ENTITY_PATHS, (entity, debugData) -> PathfindingDebugRenderer.render((double)cameraX, (double)cameraY, (double)cameraZ, (Path)debugData.path(), (float)debugData.maxNodeDistance()));
    }

    private static void render(double cameraX, double cameraY, double cameraZ, Path path, float maxNodeDistance) {
        PathfindingDebugRenderer.drawPath((Path)path, (float)maxNodeDistance, (boolean)true, (boolean)true, (double)cameraX, (double)cameraY, (double)cameraZ);
    }

    public static void drawPath(Path path, float maxNodeDistance, boolean bl, boolean bl2, double cameraX, double cameraY, double cameraZ) {
        PathfindingDebugRenderer.drawPathLines((Path)path, (double)cameraX, (double)cameraY, (double)cameraZ);
        BlockPos blockPos = path.getTarget();
        if (PathfindingDebugRenderer.getManhattanDistance((BlockPos)blockPos, (double)cameraX, (double)cameraY, (double)cameraZ) <= 80.0f) {
            GizmoDrawing.box((Box)new Box((double)((float)blockPos.getX() + 0.25f), (double)((float)blockPos.getY() + 0.25f), (double)blockPos.getZ() + 0.25, (double)((float)blockPos.getX() + 0.75f), (double)((float)blockPos.getY() + 0.75f), (double)((float)blockPos.getZ() + 0.75f)), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.5f, (float)0.0f, (float)1.0f, (float)0.0f)));
            for (int i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance((BlockPos)pathNode.getBlockPos(), (double)cameraX, (double)cameraY, (double)cameraZ) <= 80.0f)) continue;
                float f = i == path.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float g = i == path.getCurrentNodeIndex() ? 0.0f : 1.0f;
                Box box = new Box((double)((float)pathNode.x + 0.5f - maxNodeDistance), (double)((float)pathNode.y + 0.01f * (float)i), (double)((float)pathNode.z + 0.5f - maxNodeDistance), (double)((float)pathNode.x + 0.5f + maxNodeDistance), (double)((float)pathNode.y + 0.25f + 0.01f * (float)i), (double)((float)pathNode.z + 0.5f + maxNodeDistance));
                GizmoDrawing.box((Box)box, (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.5f, (float)f, (float)0.0f, (float)g)));
            }
        }
        Path.DebugNodeInfo debugNodeInfo = path.getDebugNodeInfos();
        if (bl && debugNodeInfo != null) {
            for (PathNode pathNode2 : debugNodeInfo.closedSet()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance((BlockPos)pathNode2.getBlockPos(), (double)cameraX, (double)cameraY, (double)cameraZ) <= 80.0f)) continue;
                GizmoDrawing.box((Box)new Box((double)((float)pathNode2.x + 0.5f - maxNodeDistance / 2.0f), (double)((float)pathNode2.y + 0.01f), (double)((float)pathNode2.z + 0.5f - maxNodeDistance / 2.0f), (double)((float)pathNode2.x + 0.5f + maxNodeDistance / 2.0f), (double)pathNode2.y + 0.1, (double)((float)pathNode2.z + 0.5f + maxNodeDistance / 2.0f)), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.5f, (float)1.0f, (float)0.8f, (float)0.8f)));
            }
            for (PathNode pathNode2 : debugNodeInfo.openSet()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance((BlockPos)pathNode2.getBlockPos(), (double)cameraX, (double)cameraY, (double)cameraZ) <= 80.0f)) continue;
                GizmoDrawing.box((Box)new Box((double)((float)pathNode2.x + 0.5f - maxNodeDistance / 2.0f), (double)((float)pathNode2.y + 0.01f), (double)((float)pathNode2.z + 0.5f - maxNodeDistance / 2.0f), (double)((float)pathNode2.x + 0.5f + maxNodeDistance / 2.0f), (double)pathNode2.y + 0.1, (double)((float)pathNode2.z + 0.5f + maxNodeDistance / 2.0f)), (DrawStyle)DrawStyle.filled((int)ColorHelper.fromFloats((float)0.5f, (float)0.8f, (float)1.0f, (float)1.0f)));
            }
        }
        if (bl2) {
            for (int j = 0; j < path.getLength(); ++j) {
                PathNode pathNode3 = path.getNode(j);
                if (!(PathfindingDebugRenderer.getManhattanDistance((BlockPos)pathNode3.getBlockPos(), (double)cameraX, (double)cameraY, (double)cameraZ) <= 80.0f)) continue;
                GizmoDrawing.text((String)String.valueOf(pathNode3.type), (Vec3d)new Vec3d((double)pathNode3.x + 0.5, (double)pathNode3.y + 0.75, (double)pathNode3.z + 0.5), (TextGizmo.Style)TextGizmo.Style.left().scaled(0.32f)).ignoreOcclusion();
                GizmoDrawing.text((String)String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode3.penalty)), (Vec3d)new Vec3d((double)pathNode3.x + 0.5, (double)pathNode3.y + 0.25, (double)pathNode3.z + 0.5), (TextGizmo.Style)TextGizmo.Style.left().scaled(0.32f)).ignoreOcclusion();
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
            if (PathfindingDebugRenderer.getManhattanDistance((BlockPos)pathNode.getBlockPos(), (double)cameraX, (double)cameraY, (double)cameraZ) > 80.0f) {
                vec3d = pathNode.getPos();
                continue;
            }
            float f = (float)i / (float)path.getLength() * 0.33f;
            int j = ColorHelper.fullAlpha((int)MathHelper.hsvToRgb((float)f, (float)0.9f, (float)0.9f));
            GizmoDrawing.arrow((Vec3d)vec3d.add(0.5, 0.5, 0.5), (Vec3d)pathNode.getPos().add(0.5, 0.5, 0.5), (int)j);
            vec3d = pathNode.getPos();
        }
    }

    private static float getManhattanDistance(BlockPos pos, double x, double y, double z) {
        return (float)(Math.abs((double)pos.getX() - x) + Math.abs((double)pos.getY() - y) + Math.abs((double)pos.getZ() - z));
    }

    private static /* synthetic */ void method_75445(DebugDataStore debugDataStore, double d, double e, double f, Entity entity) {
        EntityPathDebugData entityPathDebugData = (EntityPathDebugData)debugDataStore.getEntityData(DebugSubscriptionTypes.ENTITY_PATHS, entity);
        if (entityPathDebugData != null) {
            PathfindingDebugRenderer.render((double)d, (double)e, (double)f, (Path)entityPathDebugData.path(), (float)entityPathDebugData.maxNodeDistance());
        }
    }
}

