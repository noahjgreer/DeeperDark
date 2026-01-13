/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$DrawMode
 *  com.mojang.blaze3d.vertex.VertexFormat$IndexType
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.BuiltBuffer$DrawParameters
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.BufferAllocator
 *  net.minecraft.client.util.BufferAllocator$CloseableBuffer
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteOrder;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BufferBuilder
implements VertexConsumer {
    private static final int field_61051 = 0xFFFFFF;
    private static final long field_52068 = -1L;
    private static final long field_52069 = -1L;
    private static final boolean LITTLE_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
    private final BufferAllocator allocator;
    private long vertexPointer = -1L;
    private int vertexCount;
    private final VertexFormat vertexFormat;
    private final VertexFormat.DrawMode drawMode;
    private final boolean canSkipElementChecks;
    private final boolean hasOverlay;
    private final int vertexSizeByte;
    private final int requiredMask;
    private final int[] offsetsByElementId;
    private int currentMask;
    private boolean building = true;

    public BufferBuilder(BufferAllocator allocator, VertexFormat.DrawMode drawMode, VertexFormat vertexFormat) {
        if (!vertexFormat.contains(VertexFormatElement.POSITION)) {
            throw new IllegalArgumentException("Cannot build mesh with no position element");
        }
        this.allocator = allocator;
        this.drawMode = drawMode;
        this.vertexFormat = vertexFormat;
        this.vertexSizeByte = vertexFormat.getVertexSize();
        this.requiredMask = vertexFormat.getElementsMask() & ~VertexFormatElement.POSITION.mask();
        this.offsetsByElementId = vertexFormat.getOffsetsByElement();
        boolean bl = vertexFormat == VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL;
        boolean bl2 = vertexFormat == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
        this.canSkipElementChecks = bl || bl2;
        this.hasOverlay = bl;
    }

    public @Nullable BuiltBuffer endNullable() {
        this.ensureBuilding();
        this.endVertex();
        BuiltBuffer builtBuffer = this.build();
        this.building = false;
        this.vertexPointer = -1L;
        return builtBuffer;
    }

    public BuiltBuffer end() {
        BuiltBuffer builtBuffer = this.endNullable();
        if (builtBuffer == null) {
            throw new IllegalStateException("BufferBuilder was empty");
        }
        return builtBuffer;
    }

    private void ensureBuilding() {
        if (!this.building) {
            throw new IllegalStateException("Not building!");
        }
    }

    private @Nullable BuiltBuffer build() {
        if (this.vertexCount == 0) {
            return null;
        }
        BufferAllocator.CloseableBuffer closeableBuffer = this.allocator.getAllocated();
        if (closeableBuffer == null) {
            return null;
        }
        int i = this.drawMode.getIndexCount(this.vertexCount);
        VertexFormat.IndexType indexType = VertexFormat.IndexType.smallestFor((int)this.vertexCount);
        return new BuiltBuffer(closeableBuffer, new BuiltBuffer.DrawParameters(this.vertexFormat, this.vertexCount, i, this.drawMode, indexType));
    }

    private long beginVertex() {
        long l;
        this.ensureBuilding();
        this.endVertex();
        if (this.vertexCount >= 0xFFFFFF) {
            throw new IllegalStateException("Trying to write too many vertices (>16777215) into BufferBuilder");
        }
        ++this.vertexCount;
        this.vertexPointer = l = this.allocator.allocate(this.vertexSizeByte);
        return l;
    }

    private long beginElement(VertexFormatElement element) {
        int i = this.currentMask;
        int j = i & ~element.mask();
        if (j == i) {
            return -1L;
        }
        this.currentMask = j;
        long l = this.vertexPointer;
        if (l == -1L) {
            throw new IllegalArgumentException("Not currently building vertex");
        }
        return l + (long)this.offsetsByElementId[element.id()];
    }

    private void endVertex() {
        if (this.vertexCount == 0) {
            return;
        }
        if (this.currentMask != 0) {
            String string = VertexFormatElement.elementsFromMask((int)this.currentMask).map(arg_0 -> ((VertexFormat)this.vertexFormat).getElementName(arg_0)).collect(Collectors.joining(", "));
            throw new IllegalStateException("Missing elements in vertex: " + string);
        }
        if (this.drawMode == VertexFormat.DrawMode.LINES) {
            long l = this.allocator.allocate(this.vertexSizeByte);
            MemoryUtil.memCopy((long)(l - (long)this.vertexSizeByte), (long)l, (long)this.vertexSizeByte);
            ++this.vertexCount;
        }
    }

    private static void putColor(long pointer, int argb) {
        int i = ColorHelper.toAbgr((int)argb);
        MemoryUtil.memPutInt((long)pointer, (int)(LITTLE_ENDIAN ? i : Integer.reverseBytes(i)));
    }

    private static void putInt(long pointer, int i) {
        if (LITTLE_ENDIAN) {
            MemoryUtil.memPutInt((long)pointer, (int)i);
        } else {
            MemoryUtil.memPutShort((long)pointer, (short)((short)(i & 0xFFFF)));
            MemoryUtil.memPutShort((long)(pointer + 2L), (short)((short)(i >> 16 & 0xFFFF)));
        }
    }

    public VertexConsumer vertex(float x, float y, float z) {
        long l = this.beginVertex() + (long)this.offsetsByElementId[VertexFormatElement.POSITION.id()];
        this.currentMask = this.requiredMask;
        MemoryUtil.memPutFloat((long)l, (float)x);
        MemoryUtil.memPutFloat((long)(l + 4L), (float)y);
        MemoryUtil.memPutFloat((long)(l + 8L), (float)z);
        return this;
    }

    public VertexConsumer color(int red, int green, int blue, int alpha) {
        long l = this.beginElement(VertexFormatElement.COLOR);
        if (l != -1L) {
            MemoryUtil.memPutByte((long)l, (byte)((byte)red));
            MemoryUtil.memPutByte((long)(l + 1L), (byte)((byte)green));
            MemoryUtil.memPutByte((long)(l + 2L), (byte)((byte)blue));
            MemoryUtil.memPutByte((long)(l + 3L), (byte)((byte)alpha));
        }
        return this;
    }

    public VertexConsumer color(int argb) {
        long l = this.beginElement(VertexFormatElement.COLOR);
        if (l != -1L) {
            BufferBuilder.putColor((long)l, (int)argb);
        }
        return this;
    }

    public VertexConsumer texture(float u, float v) {
        long l = this.beginElement(VertexFormatElement.UV0);
        if (l != -1L) {
            MemoryUtil.memPutFloat((long)l, (float)u);
            MemoryUtil.memPutFloat((long)(l + 4L), (float)v);
        }
        return this;
    }

    public VertexConsumer overlay(int u, int v) {
        return this.uv((short)u, (short)v, VertexFormatElement.UV1);
    }

    public VertexConsumer overlay(int uv) {
        long l = this.beginElement(VertexFormatElement.UV1);
        if (l != -1L) {
            BufferBuilder.putInt((long)l, (int)uv);
        }
        return this;
    }

    public VertexConsumer light(int u, int v) {
        return this.uv((short)u, (short)v, VertexFormatElement.UV2);
    }

    public VertexConsumer light(int uv) {
        long l = this.beginElement(VertexFormatElement.UV2);
        if (l != -1L) {
            BufferBuilder.putInt((long)l, (int)uv);
        }
        return this;
    }

    private VertexConsumer uv(short u, short v, VertexFormatElement element) {
        long l = this.beginElement(element);
        if (l != -1L) {
            MemoryUtil.memPutShort((long)l, (short)u);
            MemoryUtil.memPutShort((long)(l + 2L), (short)v);
        }
        return this;
    }

    public VertexConsumer normal(float x, float y, float z) {
        long l = this.beginElement(VertexFormatElement.NORMAL);
        if (l != -1L) {
            MemoryUtil.memPutByte((long)l, (byte)BufferBuilder.floatToByte((float)x));
            MemoryUtil.memPutByte((long)(l + 1L), (byte)BufferBuilder.floatToByte((float)y));
            MemoryUtil.memPutByte((long)(l + 2L), (byte)BufferBuilder.floatToByte((float)z));
        }
        return this;
    }

    public VertexConsumer lineWidth(float width) {
        long l = this.beginElement(VertexFormatElement.LINE_WIDTH);
        if (l != -1L) {
            MemoryUtil.memPutFloat((long)l, (float)width);
        }
        return this;
    }

    private static byte floatToByte(float f) {
        return (byte)((int)(MathHelper.clamp((float)f, (float)-1.0f, (float)1.0f) * 127.0f) & 0xFF);
    }

    public void vertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        if (this.canSkipElementChecks) {
            long m;
            long l = this.beginVertex();
            MemoryUtil.memPutFloat((long)(l + 0L), (float)x);
            MemoryUtil.memPutFloat((long)(l + 4L), (float)y);
            MemoryUtil.memPutFloat((long)(l + 8L), (float)z);
            BufferBuilder.putColor((long)(l + 12L), (int)color);
            MemoryUtil.memPutFloat((long)(l + 16L), (float)u);
            MemoryUtil.memPutFloat((long)(l + 20L), (float)v);
            if (this.hasOverlay) {
                BufferBuilder.putInt((long)(l + 24L), (int)overlay);
                m = l + 28L;
            } else {
                m = l + 24L;
            }
            BufferBuilder.putInt((long)(m + 0L), (int)light);
            MemoryUtil.memPutByte((long)(m + 4L), (byte)BufferBuilder.floatToByte((float)normalX));
            MemoryUtil.memPutByte((long)(m + 5L), (byte)BufferBuilder.floatToByte((float)normalY));
            MemoryUtil.memPutByte((long)(m + 6L), (byte)BufferBuilder.floatToByte((float)normalZ));
            return;
        }
        super.vertex(x, y, z, color, u, v, overlay, light, normalX, normalY, normalZ);
    }
}

