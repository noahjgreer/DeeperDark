/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.sound.MusicTracker
 *  net.minecraft.client.sound.MusicTracker$1
 *  net.minecraft.client.sound.MusicTracker$MusicFrequency
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.sound.MusicSound
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class MusicTracker {
    private static final int DEFAULT_TIME_UNTIL_NEXT_SONG = 100;
    private final Random random = Random.create();
    private final MinecraftClient client;
    private @Nullable SoundInstance current;
    private MusicFrequency musicFrequency;
    private float volume = 1.0f;
    private int timeUntilNextSong = 100;
    private boolean shownToast = false;

    public MusicTracker(MinecraftClient client) {
        this.client = client;
        this.musicFrequency = (MusicFrequency)client.options.getMusicFrequency().getValue();
    }

    public void tick() {
        boolean bl;
        float f = this.client.getMusicVolume();
        if (this.current != null && this.volume != f && !(bl = this.canFadeTowardsVolume(f))) {
            return;
        }
        MusicSound musicSound = this.client.getMusicInstance();
        if (musicSound == null) {
            this.timeUntilNextSong = Math.max(this.timeUntilNextSong, 100);
            return;
        }
        if (this.current != null) {
            if (MusicTracker.shouldReplace((MusicSound)musicSound, (SoundInstance)this.current)) {
                this.client.getSoundManager().stop(this.current);
                this.timeUntilNextSong = MathHelper.nextInt((Random)this.random, (int)0, (int)(musicSound.minDelay() / 2));
            }
            if (!this.client.getSoundManager().isPlaying(this.current)) {
                this.current = null;
                this.timeUntilNextSong = Math.min(this.timeUntilNextSong, this.musicFrequency.getDelayBeforePlaying(musicSound, this.random));
            }
        }
        this.timeUntilNextSong = Math.min(this.timeUntilNextSong, this.musicFrequency.getDelayBeforePlaying(musicSound, this.random));
        if (this.current == null && this.timeUntilNextSong-- <= 0) {
            this.play(musicSound);
        }
    }

    private static boolean shouldReplace(MusicSound newSound, SoundInstance currentSound) {
        return newSound.replaceCurrentMusic() && !((SoundEvent)newSound.sound().value()).id().equals((Object)currentSound.getId());
    }

    public void play(MusicSound sound) {
        SoundEvent soundEvent = (SoundEvent)sound.sound().value();
        this.current = PositionedSoundInstance.music((SoundEvent)soundEvent);
        switch (1.field_60952[this.client.getSoundManager().play(this.current).ordinal()]) {
            case 1: {
                this.client.getToastManager().onMusicTrackStart();
                this.shownToast = true;
                break;
            }
            case 2: {
                this.shownToast = false;
            }
        }
        this.timeUntilNextSong = Integer.MAX_VALUE;
    }

    public void tryShowToast() {
        if (!this.shownToast) {
            this.client.getToastManager().onMusicTrackStart();
            this.shownToast = true;
        }
    }

    public void stop(MusicSound type) {
        if (this.isPlayingType(type)) {
            this.stop();
        }
    }

    public void stop() {
        if (this.current != null) {
            this.client.getSoundManager().stop(this.current);
            this.current = null;
            this.client.getToastManager().onMusicTrackStop();
        }
        this.timeUntilNextSong += 100;
    }

    private boolean canFadeTowardsVolume(float volume) {
        if (this.current == null) {
            return false;
        }
        if (this.volume == volume) {
            return true;
        }
        if (this.volume < volume) {
            this.volume += MathHelper.clamp((float)this.volume, (float)5.0E-4f, (float)0.005f);
            if (this.volume > volume) {
                this.volume = volume;
            }
        } else {
            this.volume = 0.03f * volume + 0.97f * this.volume;
            if (Math.abs(this.volume - volume) < 1.0E-4f || this.volume < volume) {
                this.volume = volume;
            }
        }
        this.volume = MathHelper.clamp((float)this.volume, (float)0.0f, (float)1.0f);
        if (this.volume <= 1.0E-4f) {
            this.stop();
            return false;
        }
        this.client.getSoundManager().setVolume(SoundCategory.MUSIC, this.volume);
        return true;
    }

    public boolean isPlayingType(MusicSound type) {
        if (this.current == null) {
            return false;
        }
        return ((SoundEvent)type.sound().value()).id().equals((Object)this.current.getId());
    }

    public @Nullable String getCurrentMusicTranslationKey() {
        Sound sound;
        if (this.current != null && (sound = this.current.getSound()) != null) {
            return sound.getIdentifier().toShortTranslationKey();
        }
        return null;
    }

    public void setMusicFrequency(MusicFrequency musicFrequency) {
        this.musicFrequency = musicFrequency;
        this.timeUntilNextSong = this.musicFrequency.getDelayBeforePlaying(this.client.getMusicInstance(), this.random);
    }
}

