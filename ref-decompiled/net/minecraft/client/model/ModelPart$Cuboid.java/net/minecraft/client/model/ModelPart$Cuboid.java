/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package net.minecraft.client.model;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public static class ModelPart.Cuboid {
    public final ModelPart.Quad[] sides;
    public final float minX;
    public final float minY;
    public final float minZ;
    public final float maxX;
    public final float maxY;
    public final float maxZ;

    public ModelPart.Cuboid(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight, Set<Direction> sides) {
        this.minX = x;
        this.minY = y;
        this.minZ = z;
        this.maxX = x + sizeX;
        this.maxY = y + sizeY;
        this.maxZ = z + sizeZ;
        this.sides = new ModelPart.Quad[sides.size()];
        float f = x + sizeX;
        float g = y + sizeY;
        float h = z + sizeZ;
        x -= extraX;
        y -= extraY;
        z -= extraZ;
        f += extraX;
        g += extraY;
        h += extraZ;
        if (mirror) {
            float i = f;
            f = x;
            x = i;
        }
        ModelPart.Vertex vertex = new ModelPart.Vertex(x, y, z, 0.0f, 0.0f);
        ModelPart.Vertex vertex2 = new ModelPart.Vertex(f, y, z, 0.0f, 8.0f);
        ModelPart.Vertex vertex3 = new ModelPart.Vertex(f, g, z, 8.0f, 8.0f);
        ModelPart.Vertex vertex4 = new ModelPart.Vertex(x, g, z, 8.0f, 0.0f);
        ModelPart.Vertex vertex5 = new ModelPart.Vertex(x, y, h, 0.0f, 0.0f);
        ModelPart.Vertex vertex6 = new ModelPart.Vertex(f, y, h, 0.0f, 8.0f);
        ModelPart.Vertex vertex7 = new ModelPart.Vertex(f, g, h, 8.0f, 8.0f);
        ModelPart.Vertex vertex8 = new ModelPart.Vertex(x, g, h, 8.0f, 0.0f);
        float j = u;
        float k = (float)u + sizeZ;
        float l = (float)u + sizeZ + sizeX;
        float m = (float)u + sizeZ + sizeX + sizeX;
        float n = (float)u + sizeZ + sizeX + sizeZ;
        float o = (float)u + sizeZ + sizeX + sizeZ + sizeX;
        float p = v;
        float q = (float)v + sizeZ;
        float r = (float)v + sizeZ + sizeY;
        int s = 0;
        if (sides.contains(Direction.DOWN)) {
            this.sides[s++] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight, mirror, Direction.DOWN);
        }
        if (sides.contains(Direction.UP)) {
            this.sides[s++] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight, mirror, Direction.UP);
        }
        if (sides.contains(Direction.WEST)) {
            this.sides[s++] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight, mirror, Direction.WEST);
        }
        if (sides.contains(Direction.NORTH)) {
            this.sides[s++] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight, mirror, Direction.NORTH);
        }
        if (sides.contains(Direction.EAST)) {
            this.sides[s++] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST);
        }
        if (sides.contains(Direction.SOUTH)) {
            this.sides[s] = new ModelPart.Quad(new ModelPart.Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight, mirror, Direction.SOUTH);
        }
    }

    public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        Matrix4f matrix4f = entry.getPositionMatrix();
        Vector3f vector3f = new Vector3f();
        for (ModelPart.Quad quad : this.sides) {
            Vector3f vector3f2 = entry.transformNormal(quad.direction, vector3f);
            float f = vector3f2.x();
            float g = vector3f2.y();
            float h = vector3f2.z();
            for (ModelPart.Vertex vertex : quad.vertices) {
                float i = vertex.worldX();
                float j = vertex.worldY();
                float k = vertex.worldZ();
                Vector3f vector3f3 = matrix4f.transformPosition(i, j, k, vector3f);
                vertexConsumer.vertex(vector3f3.x(), vector3f3.y(), vector3f3.z(), color, vertex.u, vertex.v, overlay, light, f, g, h);
            }
        }
    }
}
