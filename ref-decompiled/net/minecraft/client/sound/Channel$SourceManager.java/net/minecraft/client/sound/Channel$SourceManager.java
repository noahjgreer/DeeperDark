/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Source;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Channel.SourceManager {
    @Nullable Source source;
    private boolean stopped;

    public boolean isStopped() {
        return this.stopped;
    }

    public Channel.SourceManager(Source source) {
        this.source = source;
    }

    public void run(Consumer<Source> action) {
        Channel.this.executor.execute(() -> {
            if (this.source != null) {
                action.accept(this.source);
            }
        });
    }

    public void close() {
        this.stopped = true;
        Channel.this.soundEngine.release(this.source);
        this.source = null;
    }
}
