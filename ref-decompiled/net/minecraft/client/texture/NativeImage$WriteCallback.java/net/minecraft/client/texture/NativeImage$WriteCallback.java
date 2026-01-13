/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.stb.STBIWriteCallback
 */
package net.minecraft.client.texture;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;
import org.lwjgl.stb.STBIWriteCallback;

@Environment(value=EnvType.CLIENT)
static class NativeImage.WriteCallback
extends STBIWriteCallback {
    private final WritableByteChannel channel;
    private @Nullable IOException exception;

    NativeImage.WriteCallback(WritableByteChannel channel) {
        this.channel = channel;
    }

    public void invoke(long context, long data, int size) {
        ByteBuffer byteBuffer = NativeImage.WriteCallback.getData((long)data, (int)size);
        try {
            this.channel.write(byteBuffer);
        }
        catch (IOException iOException) {
            this.exception = iOException;
        }
    }

    public void throwStoredException() throws IOException {
        if (this.exception != null) {
            throw this.exception;
        }
    }
}
