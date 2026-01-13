/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableLong
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.Vec3fArray;
import org.apache.commons.lang3.mutable.MutableLong;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public record BuiltBuffer.SortState(Vec3fArray centroids, VertexFormat.IndexType indexType) {
    public @Nullable BufferAllocator.CloseableBuffer sortAndStore(BufferAllocator allocator, VertexSorter sorter) {
        int[] is = sorter.sort(this.centroids);
        long l = allocator.allocate(is.length * 6 * this.indexType.size);
        IntConsumer intConsumer = this.getStorer(l, this.indexType);
        for (int i : is) {
            intConsumer.accept(i * 4 + 0);
            intConsumer.accept(i * 4 + 1);
            intConsumer.accept(i * 4 + 2);
            intConsumer.accept(i * 4 + 2);
            intConsumer.accept(i * 4 + 3);
            intConsumer.accept(i * 4 + 0);
        }
        return allocator.getAllocated();
    }

    private IntConsumer getStorer(long pointer, VertexFormat.IndexType indexType) {
        MutableLong mutableLong = new MutableLong(pointer);
        return switch (indexType) {
            default -> throw new MatchException(null, null);
            case VertexFormat.IndexType.SHORT -> i -> MemoryUtil.memPutShort((long)mutableLong.getAndAdd(2L), (short)((short)i));
            case VertexFormat.IndexType.INT -> i -> MemoryUtil.memPutInt((long)mutableLong.getAndAdd(4L), (int)i);
        };
    }
}
