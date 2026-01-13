/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.Channel
 *  net.minecraft.client.sound.Channel$SourceManager
 *  net.minecraft.client.sound.SoundEngine
 *  net.minecraft.client.sound.SoundEngine$RunMode
 *  net.minecraft.client.sound.Source
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Channel;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.Source;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Channel {
    private final Set<SourceManager> sources = Sets.newIdentityHashSet();
    final SoundEngine soundEngine;
    final Executor executor;

    public Channel(SoundEngine soundEngine, Executor executor) {
        this.soundEngine = soundEngine;
        this.executor = executor;
    }

    public CompletableFuture<// Could not load outer class - annotation placement on inner may be incorrect
    @Nullable SourceManager> createSource(SoundEngine.RunMode mode) {
        CompletableFuture<// Could not load outer class - annotation placement on inner may be incorrect
        @Nullable SourceManager> completableFuture = new CompletableFuture<SourceManager>();
        this.executor.execute(() -> {
            Source source = this.soundEngine.createSource(mode);
            if (source != null) {
                SourceManager sourceManager = new SourceManager(this, source);
                this.sources.add(sourceManager);
                completableFuture.complete(sourceManager);
            } else {
                completableFuture.complete(null);
            }
        });
        return completableFuture;
    }

    public void execute(Consumer<Stream<Source>> sourcesConsumer) {
        this.executor.execute(() -> sourcesConsumer.accept(this.sources.stream().map(source -> source.source).filter(Objects::nonNull)));
    }

    public void tick() {
        this.executor.execute(() -> {
            Iterator iterator = this.sources.iterator();
            while (iterator.hasNext()) {
                SourceManager sourceManager = (SourceManager)iterator.next();
                sourceManager.source.tick();
                if (!sourceManager.source.isStopped()) continue;
                sourceManager.close();
                iterator.remove();
            }
        });
    }

    public void close() {
        this.sources.forEach(SourceManager::close);
        this.sources.clear();
    }
}

