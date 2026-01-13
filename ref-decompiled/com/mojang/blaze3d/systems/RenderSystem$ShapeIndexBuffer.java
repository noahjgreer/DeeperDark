/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public static final class RenderSystem.ShapeIndexBuffer {
    private final int vertexCountInShape;
    private final int vertexCountInTriangulated;
    private final Triangulator triangulator;
    private @Nullable GpuBuffer indexBuffer;
    private VertexFormat.IndexType indexType = VertexFormat.IndexType.SHORT;
    private int size;

    RenderSystem.ShapeIndexBuffer(int vertexCountInShape, int vertexCountInTriangulated, Triangulator triangulator) {
        this.vertexCountInShape = vertexCountInShape;
        this.vertexCountInTriangulated = vertexCountInTriangulated;
        this.triangulator = triangulator;
    }

    public boolean isLargeEnough(int requiredSize) {
        return requiredSize <= this.size;
    }

    public GpuBuffer getIndexBuffer(int requiredSize) {
        this.grow(requiredSize);
        return this.indexBuffer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void grow(int requiredSize) {
        if (this.isLargeEnough(requiredSize)) {
            return;
        }
        requiredSize = MathHelper.roundUpToMultiple(requiredSize * 2, this.vertexCountInTriangulated);
        LOGGER.debug("Growing IndexBuffer: Old limit {}, new limit {}.", (Object)this.size, (Object)requiredSize);
        int i = requiredSize / this.vertexCountInTriangulated;
        int j = i * this.vertexCountInShape;
        VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor(j);
        int k = MathHelper.roundUpToMultiple(requiredSize * indexType.size, 4);
        ByteBuffer byteBuffer = MemoryUtil.memAlloc((int)k);
        try {
            this.indexType = indexType;
            IntConsumer intConsumer = this.getIndexConsumer(byteBuffer);
            for (int l = 0; l < requiredSize; l += this.vertexCountInTriangulated) {
                this.triangulator.accept(intConsumer, l * this.vertexCountInShape / this.vertexCountInTriangulated);
            }
            byteBuffer.flip();
            if (this.indexBuffer != null) {
                this.indexBuffer.close();
            }
            this.indexBuffer = RenderSystem.getDevice().createBuffer(() -> "Auto Storage index buffer", 64, byteBuffer);
        }
        finally {
            MemoryUtil.memFree((Buffer)byteBuffer);
        }
        this.size = requiredSize;
    }

    private IntConsumer getIndexConsumer(ByteBuffer indexBuffer) {
        switch (this.indexType) {
            case SHORT: {
                return index -> indexBuffer.putShort((short)index);
            }
        }
        return indexBuffer::putInt;
    }

    public VertexFormat.IndexType getIndexType() {
        return this.indexType;
    }

    @Environment(value=EnvType.CLIENT)
    static interface Triangulator {
        public void accept(IntConsumer var1, int var2);
    }
}
