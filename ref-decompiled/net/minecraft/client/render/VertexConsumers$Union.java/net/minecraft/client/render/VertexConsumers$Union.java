/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;

@Environment(value=EnvType.CLIENT)
record VertexConsumers.Union(VertexConsumer[] delegates) implements VertexConsumer
{
    VertexConsumers.Union {
        for (int i = 0; i < delegates.length; ++i) {
            for (int j = i + 1; j < delegates.length; ++j) {
                if (delegates[i] != delegates[j]) continue;
                throw new IllegalArgumentException("Duplicate delegates");
            }
        }
    }

    private void delegate(Consumer<VertexConsumer> action) {
        for (VertexConsumer vertexConsumer : this.delegates) {
            action.accept(vertexConsumer);
        }
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        this.delegate(vertexConsumer -> vertexConsumer.vertex(x, y, z));
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        this.delegate(vertexConsumer -> vertexConsumer.color(red, green, blue, alpha));
        return this;
    }

    @Override
    public VertexConsumer color(int argb) {
        this.delegate(vertexConsumer -> vertexConsumer.color(argb));
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        this.delegate(vertexConsumer -> vertexConsumer.texture(u, v));
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        this.delegate(vertexConsumer -> vertexConsumer.overlay(u, v));
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        this.delegate(vertexConsumer -> vertexConsumer.light(u, v));
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        this.delegate(vertexConsumer -> vertexConsumer.normal(x, y, z));
        return this;
    }

    @Override
    public VertexConsumer lineWidth(float width) {
        this.delegate(vertexConsumer -> vertexConsumer.lineWidth(width));
        return this;
    }

    @Override
    public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        this.delegate(vertexConsumer -> vertexConsumer.vertex(x, y, z, color, u, v, overlay, light, normalX, normalY, normalZ));
    }
}
