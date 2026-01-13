/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.VertexSorter
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.BuiltBuffer$DrawParameters
 *  net.minecraft.client.render.BuiltBuffer$SortState
 *  net.minecraft.client.util.BufferAllocator
 *  net.minecraft.client.util.BufferAllocator$CloseableBuffer
 *  net.minecraft.client.util.math.Vec3fArray
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.VertexSorter;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.Vec3fArray;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BuiltBuffer
implements AutoCloseable {
    private final BufferAllocator.CloseableBuffer buffer;
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BufferAllocator.CloseableBuffer sortedBuffer;
    private final DrawParameters drawParameters;

    public BuiltBuffer(BufferAllocator.CloseableBuffer buffer, DrawParameters drawParameters) {
        this.buffer = buffer;
        this.drawParameters = drawParameters;
    }

    private static Vec3fArray collectCentroids(ByteBuffer buffer, int vertexCount, VertexFormat format) {
        int i = format.getOffset(VertexFormatElement.POSITION);
        if (i == -1) {
            throw new IllegalArgumentException("Cannot identify quad centers with no position element");
        }
        FloatBuffer floatBuffer = buffer.asFloatBuffer();
        int j = format.getVertexSize() / 4;
        int k = j * 4;
        int l = vertexCount / 4;
        Vec3fArray vec3fArray = new Vec3fArray(l);
        for (int m = 0; m < l; ++m) {
            int n = m * k + i;
            int o = n + j * 2;
            float f = floatBuffer.get(n + 0);
            float g = floatBuffer.get(n + 1);
            float h = floatBuffer.get(n + 2);
            float p = floatBuffer.get(o + 0);
            float q = floatBuffer.get(o + 1);
            float r = floatBuffer.get(o + 2);
            float s = (f + p) / 2.0f;
            float t = (g + q) / 2.0f;
            float u = (h + r) / 2.0f;
            vec3fArray.set(m, s, t, u);
        }
        return vec3fArray;
    }

    public ByteBuffer getBuffer() {
        return this.buffer.getBuffer();
    }

    public @Nullable ByteBuffer getSortedBuffer() {
        return this.sortedBuffer != null ? this.sortedBuffer.getBuffer() : null;
    }

    public DrawParameters getDrawParameters() {
        return this.drawParameters;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable BuiltBuffer.SortState sortQuads(BufferAllocator allocator, VertexSorter sorter) {
        if (this.drawParameters.mode() != VertexFormat.DrawMode.QUADS) {
            return null;
        }
        Vec3fArray vec3fArray = BuiltBuffer.collectCentroids((ByteBuffer)this.buffer.getBuffer(), (int)this.drawParameters.vertexCount(), (VertexFormat)this.drawParameters.format());
        SortState sortState = new SortState(vec3fArray, this.drawParameters.indexType());
        this.sortedBuffer = sortState.sortAndStore(allocator, sorter);
        return sortState;
    }

    @Override
    public void close() {
        this.buffer.close();
        if (this.sortedBuffer != null) {
            this.sortedBuffer.close();
        }
    }
}

