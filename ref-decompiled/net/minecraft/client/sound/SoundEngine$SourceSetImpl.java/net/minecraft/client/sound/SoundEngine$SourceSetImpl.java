/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.Source;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class SoundEngine.SourceSetImpl
implements SoundEngine.SourceSet {
    private final int maxSourceCount;
    private final Set<Source> sources = Sets.newIdentityHashSet();

    public SoundEngine.SourceSetImpl(int maxSourceCount) {
        this.maxSourceCount = maxSourceCount;
    }

    @Override
    public @Nullable Source createSource() {
        if (this.sources.size() >= this.maxSourceCount) {
            if (SharedConstants.isDevelopment) {
                LOGGER.warn("Maximum sound pool size {} reached", (Object)this.maxSourceCount);
            }
            return null;
        }
        Source source = Source.create();
        if (source != null) {
            this.sources.add(source);
        }
        return source;
    }

    @Override
    public boolean release(Source source) {
        if (!this.sources.remove(source)) {
            return false;
        }
        source.close();
        return true;
    }

    @Override
    public void close() {
        this.sources.forEach(Source::close);
        this.sources.clear();
    }

    @Override
    public int getMaxSourceCount() {
        return this.maxSourceCount;
    }

    @Override
    public int getSourceCount() {
        return this.sources.size();
    }
}
