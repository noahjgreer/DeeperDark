/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.annotation.DeobfuscateClass
 */
package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.buffers.GpuBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public record GpuBufferSlice(GpuBuffer buffer, long offset, long length) {
    private final GpuBuffer buffer;
    private final long offset;
    private final long length;

    public GpuBufferSlice(GpuBuffer buffer, long offset, long length) {
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    public GpuBufferSlice slice(long offset, long length) {
        if (offset < 0L || length < 0L || offset + length > this.length) {
            throw new IllegalArgumentException("Offset of " + offset + " and length " + length + " would put new slice outside existing slice's range (of " + this.offset + "," + this.length + ")");
        }
        return new GpuBufferSlice(this.buffer, this.offset + offset, length);
    }

    public GpuBuffer buffer() {
        return this.buffer;
    }

    public long offset() {
        return this.offset;
    }

    public long length() {
        return this.length;
    }
}

