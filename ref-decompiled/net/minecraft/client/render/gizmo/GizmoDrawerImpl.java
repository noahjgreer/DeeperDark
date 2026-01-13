/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.gizmo.GizmoDrawerImpl
 *  net.minecraft.client.render.gizmo.GizmoDrawerImpl$Division
 *  net.minecraft.client.render.gizmo.GizmoDrawerImpl$Line
 *  net.minecraft.client.render.gizmo.GizmoDrawerImpl$Point
 *  net.minecraft.client.render.gizmo.GizmoDrawerImpl$Polygon
 *  net.minecraft.client.render.gizmo.GizmoDrawerImpl$Quad
 *  net.minecraft.client.render.gizmo.GizmoDrawerImpl$Text
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.debug.gizmo.GizmoDrawer
 *  net.minecraft.world.debug.gizmo.TextGizmo$Style
 *  org.joml.Matrix4f
 */
package net.minecraft.client.render.gizmo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.gizmo.GizmoDrawerImpl;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawer;
import net.minecraft.world.debug.gizmo.TextGizmo;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class GizmoDrawerImpl
implements GizmoDrawer {
    private final Division opaque = new Division(true);
    private final Division transparent = new Division(false);
    private boolean empty = true;

    private Division getDivision(int color) {
        if (ColorHelper.getAlpha((int)color) < 255) {
            return this.transparent;
        }
        return this.opaque;
    }

    public void addPoint(Vec3d pos, int color, float size) {
        this.getDivision((int)color).points.add(new Point(pos, color, size));
        this.empty = false;
    }

    public void addLine(Vec3d start, Vec3d end, int color, float width) {
        this.getDivision((int)color).lines.add(new Line(start, end, color, width));
        this.empty = false;
    }

    public void addPolygon(Vec3d[] vertices, int color) {
        this.getDivision((int)color).triangleFans.add(new Polygon(vertices, color));
        this.empty = false;
    }

    public void addQuad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, int color) {
        this.getDivision((int)color).quads.add(new Quad(a, b, c, d, color));
        this.empty = false;
    }

    public void addText(Vec3d pos, String text, TextGizmo.Style style) {
        this.getDivision((int)style.color()).texts.add(new Text(pos, text, style));
        this.empty = false;
    }

    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState, Matrix4f posMatrix) {
        this.opaque.draw(matrices, vertexConsumers, cameraRenderState, posMatrix);
        this.transparent.draw(matrices, vertexConsumers, cameraRenderState, posMatrix);
    }

    public boolean isEmpty() {
        return this.empty;
    }
}

