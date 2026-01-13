/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GpuDeviceInfo;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public class VertexFormat {
    public static final int UNKNOWN_ELEMENT = -1;
    private final List<VertexFormatElement> elements;
    private final List<String> names;
    private final int vertexSize;
    private final int elementsMask;
    private final int[] offsetsByElement = new int[32];
    private @Nullable GpuBuffer immediateDrawVertexBuffer;
    private @Nullable GpuBuffer immediateDrawIndexBuffer;

    VertexFormat(List<VertexFormatElement> elements, List<String> names, IntList offsets, int vertexSize) {
        this.elements = elements;
        this.names = names;
        this.vertexSize = vertexSize;
        this.elementsMask = elements.stream().mapToInt(VertexFormatElement::mask).reduce(0, (a, b) -> a | b);
        for (int i = 0; i < this.offsetsByElement.length; ++i) {
            VertexFormatElement vertexFormatElement = VertexFormatElement.byId(i);
            int j = vertexFormatElement != null ? elements.indexOf(vertexFormatElement) : -1;
            this.offsetsByElement[i] = j != -1 ? offsets.getInt(j) : -1;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String toString() {
        return "VertexFormat" + String.valueOf(this.names);
    }

    public int getVertexSize() {
        return this.vertexSize;
    }

    public List<VertexFormatElement> getElements() {
        return this.elements;
    }

    public List<String> getElementAttributeNames() {
        return this.names;
    }

    public int[] getOffsetsByElement() {
        return this.offsetsByElement;
    }

    public int getOffset(VertexFormatElement element) {
        return this.offsetsByElement[element.id()];
    }

    public boolean contains(VertexFormatElement element) {
        return (this.elementsMask & element.mask()) != 0;
    }

    public int getElementsMask() {
        return this.elementsMask;
    }

    public String getElementName(VertexFormatElement element) {
        int i = this.elements.indexOf(element);
        if (i == -1) {
            throw new IllegalArgumentException(String.valueOf(element) + " is not contained in format");
        }
        return this.names.get(i);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VertexFormat)) return false;
        VertexFormat vertexFormat = (VertexFormat)o;
        if (this.elementsMask != vertexFormat.elementsMask) return false;
        if (this.vertexSize != vertexFormat.vertexSize) return false;
        if (!this.names.equals(vertexFormat.names)) return false;
        if (!Arrays.equals(this.offsetsByElement, vertexFormat.offsetsByElement)) return false;
        return true;
    }

    public int hashCode() {
        return this.elementsMask * 31 + Arrays.hashCode(this.offsetsByElement);
    }

    private static GpuBuffer uploadToBuffer(@Nullable GpuBuffer gpuBuffer, ByteBuffer data, @GpuBuffer.Usage int usage, Supplier<String> labelGetter) {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        if (GpuDeviceInfo.get(gpuDevice).requiresRecreateOnUploadToBuffer()) {
            if (gpuBuffer != null) {
                gpuBuffer.close();
            }
            return gpuDevice.createBuffer(labelGetter, usage, data);
        }
        if (gpuBuffer == null) {
            gpuBuffer = gpuDevice.createBuffer(labelGetter, usage, data);
        } else {
            CommandEncoder commandEncoder = gpuDevice.createCommandEncoder();
            if (gpuBuffer.size() < (long)data.remaining()) {
                gpuBuffer.close();
                gpuBuffer = gpuDevice.createBuffer(labelGetter, usage, data);
            } else {
                commandEncoder.writeToBuffer(gpuBuffer.slice(), data);
            }
        }
        return gpuBuffer;
    }

    public GpuBuffer uploadImmediateVertexBuffer(ByteBuffer data) {
        this.immediateDrawVertexBuffer = VertexFormat.uploadToBuffer(this.immediateDrawVertexBuffer, data, 40, () -> "Immediate vertex buffer for " + String.valueOf(this));
        return this.immediateDrawVertexBuffer;
    }

    public GpuBuffer uploadImmediateIndexBuffer(ByteBuffer data) {
        this.immediateDrawIndexBuffer = VertexFormat.uploadToBuffer(this.immediateDrawIndexBuffer, data, 72, () -> "Immediate index buffer for " + String.valueOf(this));
        return this.immediateDrawIndexBuffer;
    }

    @Environment(value=EnvType.CLIENT)
    @DeobfuscateClass
    public static class Builder {
        private final ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
        private final IntList offsets = new IntArrayList();
        private int offset;

        Builder() {
        }

        public Builder add(String name, VertexFormatElement element) {
            this.elements.put((Object)name, (Object)element);
            this.offsets.add(this.offset);
            this.offset += element.byteSize();
            return this;
        }

        public Builder padding(int padding) {
            this.offset += padding;
            return this;
        }

        public VertexFormat build() {
            ImmutableMap immutableMap = this.elements.buildOrThrow();
            ImmutableList immutableList = immutableMap.values().asList();
            ImmutableList immutableList2 = immutableMap.keySet().asList();
            return new VertexFormat((List<VertexFormatElement>)immutableList, (List<String>)immutableList2, this.offsets, this.offset);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class DrawMode
    extends Enum<DrawMode> {
        public static final /* enum */ DrawMode LINES = new DrawMode(2, 2, false);
        public static final /* enum */ DrawMode DEBUG_LINES = new DrawMode(2, 2, false);
        public static final /* enum */ DrawMode DEBUG_LINE_STRIP = new DrawMode(2, 1, true);
        public static final /* enum */ DrawMode POINTS = new DrawMode(1, 1, false);
        public static final /* enum */ DrawMode TRIANGLES = new DrawMode(3, 3, false);
        public static final /* enum */ DrawMode TRIANGLE_STRIP = new DrawMode(3, 1, true);
        public static final /* enum */ DrawMode TRIANGLE_FAN = new DrawMode(3, 1, true);
        public static final /* enum */ DrawMode QUADS = new DrawMode(4, 4, false);
        public final int firstVertexCount;
        public final int additionalVertexCount;
        public final boolean shareVertices;
        private static final /* synthetic */ DrawMode[] field_27386;

        public static DrawMode[] values() {
            return (DrawMode[])field_27386.clone();
        }

        public static DrawMode valueOf(String string) {
            return Enum.valueOf(DrawMode.class, string);
        }

        private DrawMode(int firstVertexCount, int additionalVertexCount, boolean shareVertices) {
            this.firstVertexCount = firstVertexCount;
            this.additionalVertexCount = additionalVertexCount;
            this.shareVertices = shareVertices;
        }

        public int getIndexCount(int vertexCount) {
            return switch (this.ordinal()) {
                case 1, 2, 3, 4, 5, 6 -> vertexCount;
                case 0, 7 -> vertexCount / 4 * 6;
                default -> 0;
            };
        }

        private static /* synthetic */ DrawMode[] method_36817() {
            return new DrawMode[]{LINES, DEBUG_LINES, DEBUG_LINE_STRIP, POINTS, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
        }

        static {
            field_27386 = DrawMode.method_36817();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class IndexType
    extends Enum<IndexType> {
        public static final /* enum */ IndexType SHORT = new IndexType(2);
        public static final /* enum */ IndexType INT = new IndexType(4);
        public final int size;
        private static final /* synthetic */ IndexType[] field_27376;

        public static IndexType[] values() {
            return (IndexType[])field_27376.clone();
        }

        public static IndexType valueOf(String string) {
            return Enum.valueOf(IndexType.class, string);
        }

        private IndexType(int size) {
            this.size = size;
        }

        public static IndexType smallestFor(int i) {
            if ((i & 0xFFFF0000) != 0) {
                return INT;
            }
            return SHORT;
        }

        private static /* synthetic */ IndexType[] method_36816() {
            return new IndexType[]{SHORT, INT};
        }

        static {
            field_27376 = IndexType.method_36816();
        }
    }
}
