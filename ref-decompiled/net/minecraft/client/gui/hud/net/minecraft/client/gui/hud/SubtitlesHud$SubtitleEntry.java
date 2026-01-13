/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class SubtitlesHud.SubtitleEntry {
    private final Text text;
    private final float range;
    private final List<SubtitlesHud.SoundEntry> sounds = new ArrayList<SubtitlesHud.SoundEntry>();

    public SubtitlesHud.SubtitleEntry(Text text, float range, Vec3d pos) {
        this.text = text;
        this.range = range;
        this.sounds.add(new SubtitlesHud.SoundEntry(pos, Util.getMeasuringTimeMs()));
    }

    public Text getText() {
        return this.text;
    }

    public  @Nullable SubtitlesHud.SoundEntry getNearestSound(Vec3d pos) {
        if (this.sounds.isEmpty()) {
            return null;
        }
        if (this.sounds.size() == 1) {
            return this.sounds.getFirst();
        }
        return this.sounds.stream().min(Comparator.comparingDouble(soundPos -> soundPos.location().distanceTo(pos))).orElse(null);
    }

    public void reset(Vec3d pos) {
        this.sounds.removeIf(sound -> pos.equals(sound.location()));
        this.sounds.add(new SubtitlesHud.SoundEntry(pos, Util.getMeasuringTimeMs()));
    }

    public boolean canHearFrom(Vec3d pos) {
        if (Float.isInfinite(this.range)) {
            return true;
        }
        if (this.sounds.isEmpty()) {
            return false;
        }
        SubtitlesHud.SoundEntry soundEntry = this.getNearestSound(pos);
        if (soundEntry == null) {
            return false;
        }
        return pos.isInRange(soundEntry.location, this.range);
    }

    public void removeExpired(double expiry) {
        long l = Util.getMeasuringTimeMs();
        this.sounds.removeIf(sound -> (double)(l - sound.time()) > expiry);
    }

    public boolean hasSounds() {
        return !this.sounds.isEmpty();
    }
}
