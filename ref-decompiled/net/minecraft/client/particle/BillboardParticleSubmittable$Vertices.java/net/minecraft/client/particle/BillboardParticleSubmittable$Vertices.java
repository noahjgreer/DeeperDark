/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticleSubmittable;

@Environment(value=EnvType.CLIENT)
static class BillboardParticleSubmittable.Vertices {
    private int maxVertices = 1024;
    private float[] floatData = new float[12288];
    private int[] intData = new int[2048];
    private int nextVertexIndex;

    BillboardParticleSubmittable.Vertices() {
    }

    public void vertex(float x, float y, float z, float rotationX, float rotationY, float rotationZ, float rotationW, float size, float minU, float maxU, float minV, float maxV, int color, int brightness) {
        if (this.nextVertexIndex >= this.maxVertices) {
            this.increaseCapacity();
        }
        int i = this.nextVertexIndex * 12;
        this.floatData[i++] = x;
        this.floatData[i++] = y;
        this.floatData[i++] = z;
        this.floatData[i++] = rotationX;
        this.floatData[i++] = rotationY;
        this.floatData[i++] = rotationZ;
        this.floatData[i++] = rotationW;
        this.floatData[i++] = size;
        this.floatData[i++] = minU;
        this.floatData[i++] = maxU;
        this.floatData[i++] = minV;
        this.floatData[i] = maxV;
        i = this.nextVertexIndex * 2;
        this.intData[i++] = color;
        this.intData[i] = brightness;
        ++this.nextVertexIndex;
    }

    public void render(BillboardParticleSubmittable.Consumer vertexConsumer) {
        for (int i = 0; i < this.nextVertexIndex; ++i) {
            int j = i * 12;
            int k = i * 2;
            vertexConsumer.consume(this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j++], this.floatData[j], this.intData[k++], this.intData[k]);
        }
    }

    public void reset() {
        this.nextVertexIndex = 0;
    }

    private void increaseCapacity() {
        this.maxVertices *= 2;
        this.floatData = Arrays.copyOf(this.floatData, this.maxVertices * 12);
        this.intData = Arrays.copyOf(this.intData, this.maxVertices * 2);
    }

    public int nextVertexIndex() {
        return this.nextVertexIndex;
    }
}
