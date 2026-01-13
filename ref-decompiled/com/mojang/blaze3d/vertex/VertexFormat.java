/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$Usage
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Builder
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.GpuDeviceInfo
 *  net.minecraft.util.annotation.DeobfuscateClass
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
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

/*
 * Exception performing whole class analysis ignored.
 */
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
            VertexFormatElement vertexFormatElement = VertexFormatElement.byId((int)i);
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
        return (String)this.names.get(i);
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
        if (GpuDeviceInfo.get((GpuDevice)gpuDevice).requiresRecreateOnUploadToBuffer()) {
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
        this.immediateDrawVertexBuffer = VertexFormat.uploadToBuffer((GpuBuffer)this.immediateDrawVertexBuffer, (ByteBuffer)data, (int)40, () -> "Immediate vertex buffer for " + String.valueOf(this));
        return this.immediateDrawVertexBuffer;
    }

    public GpuBuffer uploadImmediateIndexBuffer(ByteBuffer data) {
        this.immediateDrawIndexBuffer = VertexFormat.uploadToBuffer((GpuBuffer)this.immediateDrawIndexBuffer, (ByteBuffer)data, (int)72, () -> "Immediate index buffer for " + String.valueOf(this));
        return this.immediateDrawIndexBuffer;
    }
}

