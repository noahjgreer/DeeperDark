/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.util;

import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class BufferAllocator.CloseableBuffer
implements AutoCloseable {
    private final long offset;
    private final int size;
    private final int clearCount;
    private boolean closed;

    BufferAllocator.CloseableBuffer(long offset, int size, int clearCount) {
        this.offset = offset;
        this.size = size;
        this.clearCount = clearCount;
    }

    public ByteBuffer getBuffer() {
        if (!BufferAllocator.this.clearCountEquals(this.clearCount)) {
            throw new IllegalStateException("Buffer is no longer valid");
        }
        return MemoryUtil.memByteBuffer((long)(BufferAllocator.this.pointer + this.offset), (int)this.size);
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (BufferAllocator.this.clearCountEquals(this.clearCount)) {
            BufferAllocator.this.clearIfUnreferenced();
        }
    }
}
