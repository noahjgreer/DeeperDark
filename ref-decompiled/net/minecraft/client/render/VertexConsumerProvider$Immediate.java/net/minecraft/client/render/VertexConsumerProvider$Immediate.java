/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class VertexConsumerProvider.Immediate
implements VertexConsumerProvider {
    protected final BufferAllocator allocator;
    protected final SequencedMap<RenderLayer, BufferAllocator> layerBuffers;
    protected final Map<RenderLayer, BufferBuilder> pending = new HashMap<RenderLayer, BufferBuilder>();
    protected @Nullable RenderLayer currentLayer;

    protected VertexConsumerProvider.Immediate(BufferAllocator allocator, SequencedMap<RenderLayer, BufferAllocator> layerBuffers) {
        this.allocator = allocator;
        this.layerBuffers = layerBuffers;
    }

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        BufferBuilder bufferBuilder = this.pending.get(layer);
        if (bufferBuilder != null && !layer.areVerticesNotShared()) {
            this.draw(layer, bufferBuilder);
            bufferBuilder = null;
        }
        if (bufferBuilder != null) {
            return bufferBuilder;
        }
        BufferAllocator bufferAllocator = (BufferAllocator)this.layerBuffers.get(layer);
        if (bufferAllocator != null) {
            bufferBuilder = new BufferBuilder(bufferAllocator, layer.getDrawMode(), layer.getVertexFormat());
        } else {
            if (this.currentLayer != null) {
                this.draw(this.currentLayer);
            }
            bufferBuilder = new BufferBuilder(this.allocator, layer.getDrawMode(), layer.getVertexFormat());
            this.currentLayer = layer;
        }
        this.pending.put(layer, bufferBuilder);
        return bufferBuilder;
    }

    public void drawCurrentLayer() {
        if (this.currentLayer != null) {
            this.draw(this.currentLayer);
            this.currentLayer = null;
        }
    }

    public void draw() {
        this.drawCurrentLayer();
        for (RenderLayer renderLayer : this.layerBuffers.keySet()) {
            this.draw(renderLayer);
        }
    }

    public void draw(RenderLayer layer) {
        BufferBuilder bufferBuilder = this.pending.remove(layer);
        if (bufferBuilder != null) {
            this.draw(layer, bufferBuilder);
        }
    }

    private void draw(RenderLayer layer, BufferBuilder builder) {
        BuiltBuffer builtBuffer = builder.endNullable();
        if (builtBuffer != null) {
            if (layer.isTranslucent()) {
                BufferAllocator bufferAllocator = this.layerBuffers.getOrDefault(layer, this.allocator);
                builtBuffer.sortQuads(bufferAllocator, RenderSystem.getProjectionType().getVertexSorter());
            }
            layer.draw(builtBuffer);
        }
        if (layer.equals(this.currentLayer)) {
            this.currentLayer = null;
        }
    }
}
