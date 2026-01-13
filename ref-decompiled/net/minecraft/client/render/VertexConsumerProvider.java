/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.util.BufferAllocator
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public interface VertexConsumerProvider {
    public static Immediate immediate(BufferAllocator buffer) {
        return VertexConsumerProvider.immediate((SequencedMap)Object2ObjectSortedMaps.emptyMap(), (BufferAllocator)buffer);
    }

    public static Immediate immediate(SequencedMap<RenderLayer, BufferAllocator> layerBuffers, BufferAllocator fallbackBuffer) {
        return new Immediate(fallbackBuffer, layerBuffers);
    }

    public VertexConsumer getBuffer(RenderLayer var1);
}

