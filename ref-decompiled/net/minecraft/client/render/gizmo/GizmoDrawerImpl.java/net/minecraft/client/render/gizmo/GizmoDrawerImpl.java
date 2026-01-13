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
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawer;
import net.minecraft.world.debug.gizmo.TextGizmo;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class GizmoDrawerImpl
implements GizmoDrawer {
    private final Division opaque = new Division(true);
    private final Division transparent = new Division(false);
    private boolean empty = true;

    private Division getDivision(int color) {
        if (ColorHelper.getAlpha(color) < 255) {
            return this.transparent;
        }
        return this.opaque;
    }

    @Override
    public void addPoint(Vec3d pos, int color, float size) {
        this.getDivision((int)color).points.add(new Point(pos, color, size));
        this.empty = false;
    }

    @Override
    public void addLine(Vec3d start, Vec3d end, int color, float width) {
        this.getDivision((int)color).lines.add(new Line(start, end, color, width));
        this.empty = false;
    }

    @Override
    public void addPolygon(Vec3d[] vertices, int color) {
        this.getDivision((int)color).triangleFans.add(new Polygon(vertices, color));
        this.empty = false;
    }

    @Override
    public void addQuad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, int color) {
        this.getDivision((int)color).quads.add(new Quad(a, b, c, d, color));
        this.empty = false;
    }

    @Override
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

    @Environment(value=EnvType.CLIENT)
    static final class Division
    extends Record {
        private final boolean opaque;
        final List<Line> lines;
        final List<Quad> quads;
        final List<Polygon> triangleFans;
        final List<Text> texts;
        final List<Point> points;

        Division(boolean opaque) {
            this(opaque, new ArrayList<Line>(), new ArrayList<Quad>(), new ArrayList<Polygon>(), new ArrayList<Text>(), new ArrayList<Point>());
        }

        private Division(boolean opaque, List<Line> lines, List<Quad> quads, List<Polygon> triangleFans, List<Text> texts, List<Point> points) {
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
            for (Text text : this.texts) {
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
            for (Line line : this.lines) {
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
            for (Polygon polygon : this.triangleFans) {
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
            for (Quad quad : this.quads) {
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
            for (Point point : this.points) {
                vertexConsumer.vertex(entry, (float)(point.pos.getX() - d), (float)(point.pos.getY() - e), (float)(point.pos.getZ() - f)).color(point.color()).lineWidth(point.size());
            }
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Division.class, "opaque;lines;quads;triangleFans;texts;points", "opaque", "lines", "quads", "triangleFans", "texts", "points"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Division.class, "opaque;lines;quads;triangleFans;texts;points", "opaque", "lines", "quads", "triangleFans", "texts", "points"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Division.class, "opaque;lines;quads;triangleFans;texts;points", "opaque", "lines", "quads", "triangleFans", "texts", "points"}, this, object);
        }

        public boolean opaque() {
            return this.opaque;
        }

        public List<Line> lines() {
            return this.lines;
        }

        public List<Quad> quads() {
            return this.quads;
        }

        public List<Polygon> triangleFans() {
            return this.triangleFans;
        }

        public List<Text> texts() {
            return this.texts;
        }

        public List<Point> points() {
            return this.points;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Point
    extends Record {
        final Vec3d pos;
        private final int color;
        private final float size;

        Point(Vec3d pos, int color, float size) {
            this.pos = pos;
            this.color = color;
            this.size = size;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Point.class, "pos;color;size", "pos", "color", "size"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Point.class, "pos;color;size", "pos", "color", "size"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Point.class, "pos;color;size", "pos", "color", "size"}, this, object);
        }

        public Vec3d pos() {
            return this.pos;
        }

        public int color() {
            return this.color;
        }

        public float size() {
            return this.size;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Line(Vec3d start, Vec3d end, int color, float width) {
    }

    @Environment(value=EnvType.CLIENT)
    record Polygon(Vec3d[] points, int color) {
    }

    @Environment(value=EnvType.CLIENT)
    record Quad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, int color) {
    }

    @Environment(value=EnvType.CLIENT)
    static final class Text
    extends Record {
        private final Vec3d pos;
        final String text;
        final TextGizmo.Style style;

        Text(Vec3d pos, String text, TextGizmo.Style style) {
            this.pos = pos;
            this.text = text;
            this.style = style;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Text.class, "pos;text;style", "pos", "text", "style"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Text.class, "pos;text;style", "pos", "text", "style"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Text.class, "pos;text;style", "pos", "text", "style"}, this, object);
        }

        public Vec3d pos() {
            return this.pos;
        }

        public String text() {
            return this.text;
        }

        public TextGizmo.Style style() {
            return this.style;
        }
    }
}
