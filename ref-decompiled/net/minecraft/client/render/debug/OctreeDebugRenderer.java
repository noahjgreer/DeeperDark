/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.DrawStyle
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.chunk.Octree
 *  net.minecraft.client.render.chunk.Octree$Node
 *  net.minecraft.client.render.debug.DebugRenderer$Renderer
 *  net.minecraft.client.render.debug.OctreeDebugRenderer
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.debug.DebugDataStore
 *  net.minecraft.world.debug.gizmo.GizmoDrawing
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.world.debug.gizmo.TextGizmo;
import org.apache.commons.lang3.mutable.MutableInt;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class OctreeDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public OctreeDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        Octree octree = this.client.worldRenderer.getChunkRenderingDataPreparer().getOctree();
        MutableInt mutableInt = new MutableInt(0);
        octree.visit((node, bl, i, bl2) -> this.renderNode(node, i, bl, mutableInt, bl2), frustum, 32);
    }

    private void renderNode(Octree.Node node, int i, boolean bl, MutableInt mutableInt, boolean bl2) {
        Box box = node.getBoundingBox();
        double d = box.getLengthX();
        long l = Math.round(d / 16.0);
        if (l == 1L) {
            mutableInt.add(1);
            int j = bl2 ? -16711936 : -1;
            GizmoDrawing.text((String)String.valueOf(mutableInt.intValue()), (Vec3d)box.getCenter(), (TextGizmo.Style)TextGizmo.Style.left((int)j).scaled(4.8f));
        }
        long m = l + 5L;
        GizmoDrawing.box((Box)box.contract(0.1 * (double)i), (DrawStyle)DrawStyle.stroked((int)ColorHelper.fromFloats((float)(bl ? 0.4f : 1.0f), (float)OctreeDebugRenderer.getColorComponent((long)m, (float)0.3f), (float)OctreeDebugRenderer.getColorComponent((long)m, (float)0.8f), (float)OctreeDebugRenderer.getColorComponent((long)m, (float)0.5f))));
    }

    private static float getColorComponent(long size, float gradient) {
        float f = 0.1f;
        return MathHelper.fractionalPart((float)(gradient * (float)size)) * 0.9f + 0.1f;
    }
}

