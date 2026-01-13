/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.OutlineVertexConsumerProvider
 *  net.minecraft.client.render.OutlineVertexConsumerProvider$OutlineVertexConsumer
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.util.BufferAllocator
 */
package net.minecraft.client.render;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;

@Environment(value=EnvType.CLIENT)
public class OutlineVertexConsumerProvider
implements VertexConsumerProvider {
    private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate((BufferAllocator)new BufferAllocator(1536));
    private int OUTLINE_COLOR = -1;

    public VertexConsumer getBuffer(RenderLayer layer) {
        if (layer.isOutline()) {
            VertexConsumer vertexConsumer = this.plainDrawer.getBuffer(layer);
            return new OutlineVertexConsumer(vertexConsumer, this.OUTLINE_COLOR);
        }
        Optional optional = layer.getAffectedOutline();
        if (optional.isPresent()) {
            VertexConsumer vertexConsumer2 = this.plainDrawer.getBuffer((RenderLayer)optional.get());
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
}

