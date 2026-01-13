/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.LightmapTextureManager
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.model.BakedQuad
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.client.util.math.Vector2f
 *  net.minecraft.util.math.ColorHelper
 *  org.joml.Matrix3x2fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public interface VertexConsumer {
    public VertexConsumer vertex(float var1, float var2, float var3);

    public VertexConsumer color(int var1, int var2, int var3, int var4);

    public VertexConsumer color(int var1);

    public VertexConsumer texture(float var1, float var2);

    public VertexConsumer overlay(int var1, int var2);

    public VertexConsumer light(int var1, int var2);

    public VertexConsumer normal(float var1, float var2, float var3);

    public VertexConsumer lineWidth(float var1);

    default public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        this.vertex(x, y, z);
        this.color(color);
        this.texture(u, v);
        this.overlay(overlay);
        this.light(light);
        this.normal(normalX, normalY, normalZ);
    }

    default public VertexConsumer color(float red, float green, float blue, float alpha) {
        return this.color((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), (int)(alpha * 255.0f));
    }

    default public VertexConsumer light(int uv) {
        return this.light(uv & 0xFFFF, uv >> 16 & 0xFFFF);
    }

    default public VertexConsumer overlay(int uv) {
        return this.overlay(uv & 0xFFFF, uv >> 16 & 0xFFFF);
    }

    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, float alpha, int light, int overlay) {
        this.quad(matrixEntry, quad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, red, green, blue, alpha, new int[]{light, light, light, light}, overlay);
    }

    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay) {
        Vector3fc vector3fc = quad.face().getFloatVector();
        Matrix4f matrix4f = matrixEntry.getPositionMatrix();
        Vector3f vector3f = matrixEntry.transformNormal(vector3fc, new Vector3f());
        int i = quad.lightEmission();
        for (int j = 0; j < 4; ++j) {
            Vector3fc vector3fc2 = quad.getPosition(j);
            long l = quad.getTexcoords(j);
            float f = brightnesses[j];
            int k = ColorHelper.fromFloats((float)alpha, (float)(f * red), (float)(f * green), (float)(f * blue));
            int m = LightmapTextureManager.applyEmission((int)lights[j], (int)i);
            Vector3f vector3f2 = matrix4f.transformPosition(vector3fc2, new Vector3f());
            float g = Vector2f.getX((long)l);
            float h = Vector2f.getY((long)l);
            this.vertex(vector3f2.x(), vector3f2.y(), vector3f2.z(), k, g, h, overlay, m, vector3f.x(), vector3f.y(), vector3f.z());
        }
    }

    default public VertexConsumer vertex(Vector3fc vec) {
        return this.vertex(vec.x(), vec.y(), vec.z());
    }

    default public VertexConsumer vertex(MatrixStack.Entry matrix, Vector3f vec) {
        return this.vertex(matrix, vec.x(), vec.y(), vec.z());
    }

    default public VertexConsumer vertex(MatrixStack.Entry matrix, float x, float y, float z) {
        return this.vertex((Matrix4fc)matrix.getPositionMatrix(), x, y, z);
    }

    default public VertexConsumer vertex(Matrix4fc matrix, float x, float y, float z) {
        Vector3f vector3f = matrix.transformPosition(x, y, z, new Vector3f());
        return this.vertex(vector3f.x(), vector3f.y(), vector3f.z());
    }

    default public VertexConsumer vertex(Matrix3x2fc matrix, float x, float y) {
        org.joml.Vector2f vector2f = matrix.transformPosition(x, y, new org.joml.Vector2f());
        return this.vertex(vector2f.x(), vector2f.y(), 0.0f);
    }

    default public VertexConsumer normal(MatrixStack.Entry matrix, float x, float y, float z) {
        Vector3f vector3f = matrix.transformNormal(x, y, z, new Vector3f());
        return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
    }

    default public VertexConsumer normal(MatrixStack.Entry matrix, Vector3f vec) {
        return this.normal(matrix, vec.x(), vec.y(), vec.z());
    }
}

