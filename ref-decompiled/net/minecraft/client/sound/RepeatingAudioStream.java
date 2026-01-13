/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.AudioStream
 *  net.minecraft.client.sound.RepeatingAudioStream
 *  net.minecraft.client.sound.RepeatingAudioStream$DelegateFactory
 *  net.minecraft.client.sound.RepeatingAudioStream$ReusableInputStream
 */
package net.minecraft.client.sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.RepeatingAudioStream;

@Environment(value=EnvType.CLIENT)
public class RepeatingAudioStream
implements AudioStream {
    private final DelegateFactory delegateFactory;
    private AudioStream delegate;
    private final BufferedInputStream inputStream;

    public RepeatingAudioStream(DelegateFactory delegateFactory, InputStream inputStream) throws IOException {
        this.delegateFactory = delegateFactory;
        this.inputStream = new BufferedInputStream(inputStream);
        this.inputStream.mark(Integer.MAX_VALUE);
        this.delegate = delegateFactory.create((InputStream)new ReusableInputStream((InputStream)this.inputStream));
    }

    public AudioFormat getFormat() {
        return this.delegate.getFormat();
    }

    public ByteBuffer read(int size) throws IOException {
        ByteBuffer byteBuffer = this.delegate.read(size);
        if (!byteBuffer.hasRemaining()) {
            this.delegate.close();
            this.inputStream.reset();
            this.delegate = this.delegateFactory.create((InputStream)new ReusableInputStream((InputStream)this.inputStream));
            byteBuffer = this.delegate.read(size);
        }
        return byteBuffer;
    }

    public void close() throws IOException {
        this.delegate.close();
        this.inputStream.close();
    }
}

