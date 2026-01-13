/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.render.gizmo;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.gizmo.GizmoDrawerImpl;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
static final class GizmoDrawerImpl.Division
extends Record {
    private final boolean opaque;
    final List<GizmoDrawerImpl.Line> lines;
    final List<GizmoDrawerImpl.Quad> quads;
    final List<GizmoDrawerImpl.Polygon> triangleFans;
    final List<GizmoDrawerImpl.Text> texts;
    final List<GizmoDrawerImpl.Point> points;

    GizmoDrawerImpl.Division(boolean opaque) {
        this(opaque, new ArrayList<GizmoDrawerImpl.Line>(), new ArrayList<GizmoDrawerImpl.Quad>(), new ArrayList<GizmoDrawerImpl.Polygon>(), new ArrayList<GizmoDrawerImpl.Text>(), new ArrayList<GizmoDrawerImpl.Point>());
    }

    private GizmoDrawerImpl.Division(boolean opaque, List<GizmoDrawerImpl.Line> lines, List<GizmoDrawerImpl.Quad> quads, List<GizmoDrawerImpl.Polygon> triangleFans, List<GizmoDrawerImpl.Text> texts, List<GizmoDrawerImpl.Point> points) {
        this.opaque = opaque;
        this.lines = lines;
        this.quads = quads;
        this.triangleFans = triangleFans;
        this.texts = texts;
        this.points = points;
    }

    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState, Matrix4f posMatrix) {
        this.drawQuads(matrices, vertexConsumers, cameraRenderState);
        this.drawTriangleFans(matrices, vertexConsumers, cameraRenderState);
        this.drawLines(matrices, vertexConsumers, cameraRenderState, posMatrix);
        this.drawText(matrices, vertexConsumers, cameraRenderState);
        this.drawPoints(matrices, vertexConsumers, cameraRenderState);
    }

    private void drawText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        if (!cameraRenderState.initialized) {
            return;
        }
        double d = cameraRenderState.pos.getX();
        double e = cameraRenderState.pos.getY();
        double f = cameraRenderState.pos.getZ();
        for (GizmoDrawerImpl.Text text : this.texts) {
            matrices.push();
            matrices.translate((float)(text.pos().getX() - d), (float)(text.pos().getY() - e), (float)(text.pos().getZ() - f));
            matrices.multiply((Quaternionfc)cameraRenderState.orientation);
            matrices.scale(text.style.scale() / 16.0f, -text.style.scale() / 16.0f, text.style.scale() / 16.0f);
            float g = text.style.adjustLeft().isEmpty() ? (float)(-textRenderer.getWidth(text.text)) / 2.0f : (float)(-text.style.adjustLeft().getAsDouble()) / text.style.scale();
            textRenderer.draw(text.text, g, 0.0f, text.style.color(), false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            matrices.pop();
        }
    }

    private void drawLines(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState, Matrix4f posMatrix) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.opaque ? RenderLayers.lines() : RenderLayers.linesTranslucent());
        MatrixStack.Entry entry = matrices.peek();
        Vector4f vector4f = new Vector4f();
        Vector4f vector4f2 = new Vector4f();
        Vector4f vector4f3 = new Vector4f();
        Vector4f vector4f4 = new Vector4f();
        Vector4f vector4f5 = new Vector4f();
        double d = cameraRenderState.pos.getX();
        double e = cameraRenderState.pos.getY();
        double f = cameraRenderState.pos.getZ();
        for (GizmoDrawerImpl.Line line : this.lines) {
            boolean bl2;
            vector4f.set(line.start().getX() - d, line.start().getY() - e, line.start().getZ() - f, 1.0);
            vector4f2.set(line.end().getX() - d, line.end().getY() - e, line.end().getZ() - f, 1.0);
            vector4f.mul((Matrix4fc)posMatrix, vector4f3);
            vector4f2.mul((Matrix4fc)posMatrix, vector4f4);
            boolean bl = vector4f3.z > -0.05f;
            boolean bl3 = bl2 = vector4f4.z > -0.05f;
            if (bl && bl2) continue;
            if (bl || bl2) {
                float g = vector4f4.z - vector4f3.z;
                if (Math.abs(g) < 1.0E-9f) continue;
                float h = MathHelper.clamp((-0.05f - vector4f3.z) / g, 0.0f, 1.0f);
                vector4f.lerp((Vector4fc)vector4f2, h, vector4f5);
                if (bl) {
                    vector4f.set((Vector4fc)vector4f5);
                } else {
                    vector4f2.set((Vector4fc)vector4f5);
                }
            }
            vertexConsumer.vertex(entry, vector4f.x, vector4f.y, vector4f.z).normal(entry, vector4f2.x - vector4f.x, vector4f2.y - vector4f.y, vector4f2.z - vector4f.z).color(line.color()).lineWidth(line.width());
            vertexConsumer.vertex(entry, vector4f2.x, vector4f2.y, vector4f2.z).normal(entry, vector4f2.x - vector4f.x, vector4f2.y - vector4f.y, vector4f2.z - vector4f.z).color(line.color()).lineWidth(line.width());
        }
    }

    private void drawTriangleFans(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState) {
        MatrixStack.Entry entry = matrices.peek();
        double d = cameraRenderState.pos.getX();
        double e = cameraRenderState.pos.getY();
        double f = cameraRenderState.pos.getZ();
        for (GizmoDrawerImpl.Polygon polygon : this.triangleFans) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.debugTriangleFan());
            for (Vec3d vec3d : polygon.points()) {
                vertexConsumer.vertex(entry, (float)(vec3d.getX() - d), (float)(vec3d.getY() - e), (float)(vec3d.getZ() - f)).color(polygon.color());
            }
        }
    }

    private void drawQuads(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.debugFilledBox());
        MatrixStack.Entry entry = matrices.peek();
        double d = cameraRenderState.pos.getX();
        double e = cameraRenderState.pos.getY();
        double f = cameraRenderState.pos.getZ();
        for (GizmoDrawerImpl.Quad quad : this.quads) {
            vertexConsumer.vertex(entry, (float)(quad.a().getX() - d), (float)(quad.a().getY() - e), (float)(quad.a().getZ() - f)).color(quad.color());
            vertexConsumer.vertex(entry, (float)(quad.b().getX() - d), (float)(quad.b().getY() - e), (float)(quad.b().getZ() - f)).color(quad.color());
            vertexConsumer.vertex(entry, (float)(quad.c().getX() - d), (float)(quad.c().getY() - e), (float)(quad.c().getZ() - f)).color(quad.color());
            vertexConsumer.vertex(entry, (float)(quad.d().getX() - d), (float)(quad.d().getY() - e), (float)(quad.d().getZ() - f)).color(quad.color());
        }
    }

    private void drawPoints(MatrixStack matrices, VertexConsumerProvider vertexConsumers, CameraRenderState cameraRenderState) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.debugPoint());
        MatrixStack.Entry entry = matrices.peek();
        double d = cameraRenderState.pos.getX();
        double e = cameraRenderState.pos.getY();
        double f = cameraRenderState.pos.getZ();
        for (GizmoDrawerImpl.Point point : this.points) {
            vertexConsumer.vertex(entry, (float)(point.pos.getX() - d), (float)(point.pos.getY() - e), (float)(point.pos.getZ() - f)).color(point.color()).lineWidth(point.size());
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{GizmoDrawerImpl.Division.class, "opaque;lines;quads;triangleFans;texts;points", "opaque", "lines", "quads", "triangleFans", "texts", "points"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GizmoDrawerImpl.Division.class, "opaque;lines;quads;triangleFans;texts;points", "opaque", "lines", "quads", "triangleFans", "texts", "points"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GizmoDrawerImpl.Division.class, "opaque;lines;quads;triangleFans;texts;points", "opaque", "lines", "quads", "triangleFans", "texts", "points"}, this, object);
    }

    public boolean opaque() {
        return this.opaque;
    }

    public List<GizmoDrawerImpl.Line> lines() {
        return this.lines;
    }

    public List<GizmoDrawerImpl.Quad> quads() {
        return this.quads;
    }

    public List<GizmoDrawerImpl.Polygon> triangleFans() {
        return this.triangleFans;
    }

    public List<GizmoDrawerImpl.Text> texts() {
        return this.texts;
    }

    public List<GizmoDrawerImpl.Point> points() {
        return this.points;
    }
}
