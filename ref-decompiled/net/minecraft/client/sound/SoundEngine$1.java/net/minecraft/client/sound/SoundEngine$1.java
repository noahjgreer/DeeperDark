/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.Source;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class SoundEngine.1
implements SoundEngine.SourceSet {
    SoundEngine.1() {
    }

    @Override
    public @Nullable Source createSource() {
        return null;
    }

    @Override
    public boolean release(Source source) {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public int getMaxSourceCount() {
        return 0;
    }

    @Override
    public int getSourceCount() {
        return 0;
    }
}
