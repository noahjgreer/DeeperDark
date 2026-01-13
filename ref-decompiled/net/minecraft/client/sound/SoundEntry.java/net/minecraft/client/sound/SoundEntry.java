/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SoundEntry {
    private final List<Sound> sounds;
    private final boolean replace;
    private final @Nullable String subtitle;

    public SoundEntry(List<Sound> sounds, boolean replace, @Nullable String subtitle) {
        this.sounds = sounds;
        this.replace = replace;
        this.subtitle = subtitle;
    }

    public List<Sound> getSounds() {
        return this.sounds;
    }

    public boolean canReplace() {
        return this.replace;
    }

    public @Nullable String getSubtitle() {
        return this.subtitle;
    }
}
