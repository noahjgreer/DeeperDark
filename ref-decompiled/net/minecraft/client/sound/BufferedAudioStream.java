/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.BufferedAudioStream
 *  net.minecraft.client.sound.ChannelList
 *  net.minecraft.client.sound.NonRepeatingAudioStream
 */
package net.minecraft.client.sound;

import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.ChannelList;
import net.minecraft.client.sound.NonRepeatingAudioStream;

@Environment(value=EnvType.CLIENT)
public interface BufferedAudioStream
extends NonRepeatingAudioStream {
    public static final int CHUNK_SIZE = 8192;

    public boolean read(FloatConsumer var1) throws IOException;

    default public ByteBuffer read(int size) throws IOException {
        ChannelList channelList = new ChannelList(size + 8192);
        while (this.read((FloatConsumer)channelList) && channelList.getCurrentBufferSize() < size) {
        }
        return channelList.getBuffer();
    }

    default public ByteBuffer readAll() throws IOException {
        ChannelList channelList = new ChannelList(16384);
        while (this.read((FloatConsumer)channelList)) {
        }
        return channelList.getBuffer();
    }
}

