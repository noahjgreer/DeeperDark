/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;

@Environment(value=EnvType.CLIENT)
public class OutlineVertexConsumerProvider
implements VertexConsumerProvider {
    private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferAllocator(1536));
    private int OUTLINE_COLOR = -1;

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        if (layer.isOutline()) {
            VertexConsumer vertexConsumer = this.plainDrawer.getBuffer(layer);
            return new OutlineVertexConsumer(vertexConsumer, this.OUTLINE_COLOR);
        }
        Optional<RenderLayer> optional = layer.getAffectedOutline();
        if (optional.isPresent()) {
            VertexConsumer vertexConsumer2 = this.plainDrawer.getBuffer(optional.get());
            return new OutlineVertexConsumer(vertexConsumer2, this.OUTLINE_COLOR);
        }
        throw new IllegalStateException("Can't render an outline for this rendertype!");
    }

    public void setColor(int red) {
        this.OUTLINE_COLOR = red;
    }

    public void draw() {
        this.plainDrawer.draw();
    }

    @Environment(value=EnvType.CLIENT)
    record OutlineVertexConsumer(VertexConsumer delegate, int color) implements VertexConsumer
    {
        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            this.delegate.vertex(x, y, z).color(this.color);
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            return this;
        }

        @Override
        public VertexConsumer color(int argb) {
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            this.delegate.texture(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public VertexConsumer lineWidth(float width) {
            return this;
        }
    }
}
