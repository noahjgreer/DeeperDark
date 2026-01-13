/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;

@Environment(value=EnvType.CLIENT)
static class VertexConsumers.Dual
implements VertexConsumer {
    private final VertexConsumer first;
    private final VertexConsumer second;

    public VertexConsumers.Dual(VertexConsumer first, VertexConsumer second) {
        if (first == second) {
            throw new IllegalArgumentException("Duplicate delegates");
        }
        this.first = first;
        this.second = second;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        this.first.vertex(x, y, z);
        this.second.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        this.first.color(red, green, blue, alpha);
        this.second.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer color(int argb) {
        this.first.color(argb);
        this.second.color(argb);
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        this.first.texture(u, v);
        this.second.texture(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        this.first.overlay(u, v);
        this.second.overlay(u, v);
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        this.first.light(u, v);
        this.second.light(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        this.first.normal(x, y, z);
        this.second.normal(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer lineWidth(float width) {
        this.first.lineWidth(width);
        this.second.lineWidth(width);
        return this;
    }

    @Override
    public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        this.first.vertex(x, y, z, color, u, v, overlay, light, normalX, normalY, normalZ);
        this.second.vertex(x, y, z, color, u, v, overlay, light, normalX, normalY, normalZ);
    }
}
