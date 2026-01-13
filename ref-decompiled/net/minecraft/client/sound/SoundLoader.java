/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.AudioStream
 *  net.minecraft.client.sound.OggAudioStream
 *  net.minecraft.client.sound.RepeatingAudioStream
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.SoundLoader
 *  net.minecraft.client.sound.StaticSound
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 */
package net.minecraft.client.sound;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.OggAudioStream;
import net.minecraft.client.sound.RepeatingAudioStream;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class SoundLoader {
    private final ResourceFactory resourceFactory;
    private final Map<Identifier, CompletableFuture<StaticSound>> loadedSounds = Maps.newHashMap();

    public SoundLoader(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public CompletableFuture<StaticSound> loadStatic(Identifier id) {
        return this.loadedSounds.computeIfAbsent(id, id2 -> CompletableFuture.supplyAsync(() -> {
            try (InputStream inputStream = this.resourceFactory.open(id2);){
                StaticSound staticSound;
                try (OggAudioStream nonRepeatingAudioStream = new OggAudioStream(inputStream);){
                    ByteBuffer byteBuffer = nonRepeatingAudioStream.readAll();
                    staticSound = new StaticSound(byteBuffer, nonRepeatingAudioStream.getFormat());
                }
                return staticSound;
            }
            catch (IOException iOException) {
                throw new CompletionException(iOException);
            }
        }, (Executor)Util.getDownloadWorkerExecutor()));
    }

    public CompletableFuture<AudioStream> loadStreamed(Identifier id, boolean repeatInstantly) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream inputStream = this.resourceFactory.open(id);
                return repeatInstantly ? new RepeatingAudioStream(OggAudioStream::new, inputStream) : new OggAudioStream(inputStream);
            }
            catch (IOException iOException) {
                throw new CompletionException(iOException);
            }
        }, (Executor)Util.getDownloadWorkerExecutor());
    }

    public void close() {
        this.loadedSounds.values().forEach(soundFuture -> soundFuture.thenAccept(StaticSound::close));
        this.loadedSounds.clear();
    }

    public CompletableFuture<?> loadStatic(Collection<Sound> sounds) {
        return CompletableFuture.allOf((CompletableFuture[])sounds.stream().map(sound -> this.loadStatic(sound.getLocation())).toArray(CompletableFuture[]::new));
    }
}

