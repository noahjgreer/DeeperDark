/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.BlockRenderLayer
 *  net.minecraft.client.render.BufferBuilderStorage
 *  net.minecraft.client.render.OutlineVertexConsumerProvider
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.chunk.BlockBufferAllocatorStorage
 *  net.minecraft.client.render.chunk.BlockBufferBuilderPool
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.util.BufferAllocator
 *  net.minecraft.util.Util
 */
package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SequencedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.BlockBufferBuilderPool;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Util;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BufferBuilderStorage {
    private final BlockBufferAllocatorStorage blockBufferBuilders = new BlockBufferAllocatorStorage();
    private final BlockBufferBuilderPool blockBufferBuildersPool;
    private final VertexConsumerProvider.Immediate entityVertexConsumers;
    private final VertexConsumerProvider.Immediate effectVertexConsumers;
    private final OutlineVertexConsumerProvider outlineVertexConsumers;

    public BufferBuilderStorage(int maxBlockBuildersPoolSize) {
        this.blockBufferBuildersPool = BlockBufferBuilderPool.allocate((int)maxBlockBuildersPoolSize);
        SequencedMap sequencedMap = (SequencedMap)Util.make((Object)new Object2ObjectLinkedOpenHashMap(), map -> {
            map.put((Object)TexturedRenderLayers.getEntitySolid(), (Object)this.blockBufferBuilders.get(BlockRenderLayer.SOLID));
            map.put((Object)TexturedRenderLayers.getEntityCutout(), (Object)this.blockBufferBuilders.get(BlockRenderLayer.CUTOUT));
            map.put((Object)TexturedRenderLayers.getItemTranslucentCull(), (Object)this.blockBufferBuilders.get(BlockRenderLayer.TRANSLUCENT));
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)TexturedRenderLayers.getBlockTranslucentCull());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)TexturedRenderLayers.getShieldPatterns());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)TexturedRenderLayers.getBeds());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)TexturedRenderLayers.getShulkerBoxes());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)TexturedRenderLayers.getSign());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)TexturedRenderLayers.getHangingSign());
            map.put((Object)TexturedRenderLayers.getChest(), (Object)new BufferAllocator(786432));
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)RenderLayers.armorEntityGlint());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)RenderLayers.glint());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)RenderLayers.glintTranslucent());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)RenderLayers.entityGlint());
            BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)map, (RenderLayer)RenderLayers.waterMask());
        });
        this.entityVertexConsumers = VertexConsumerProvider.immediate((SequencedMap)sequencedMap, (BufferAllocator)new BufferAllocator(786432));
        this.outlineVertexConsumers = new OutlineVertexConsumerProvider();
        SequencedMap sequencedMap2 = (SequencedMap)Util.make((Object)new Object2ObjectLinkedOpenHashMap(), objectMap -> ModelBaker.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach(renderLayer -> BufferBuilderStorage.assignBufferBuilder((Object2ObjectLinkedOpenHashMap)objectMap, (RenderLayer)renderLayer)));
        this.effectVertexConsumers = VertexConsumerProvider.immediate((SequencedMap)sequencedMap2, (BufferAllocator)new BufferAllocator(0));
    }

    private static void assignBufferBuilder(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferAllocator> builderStorage, RenderLayer layer) {
        builderStorage.put((Object)layer, (Object)new BufferAllocator(layer.getExpectedBufferSize()));
    }

    public BlockBufferAllocatorStorage getBlockBufferBuilders() {
        return this.blockBufferBuilders;
    }

    public BlockBufferBuilderPool getBlockBufferBuildersPool() {
        return this.blockBufferBuildersPool;
    }

    public VertexConsumerProvider.Immediate getEntityVertexConsumers() {
        return this.entityVertexConsumers;
    }

    public VertexConsumerProvider.Immediate getEffectVertexConsumers() {
        return this.effectVertexConsumers;
    }

    public OutlineVertexConsumerProvider getOutlineVertexConsumers() {
        return this.outlineVertexConsumers;
    }
}

