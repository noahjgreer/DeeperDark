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
import net.minecraft.client.sound.Source;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static interface SoundEngine.SourceSet {
    public @Nullable Source createSource();

    public boolean release(Source var1);

    public void close();

    public int getMaxSourceCount();

    public int getSourceCount();
}
