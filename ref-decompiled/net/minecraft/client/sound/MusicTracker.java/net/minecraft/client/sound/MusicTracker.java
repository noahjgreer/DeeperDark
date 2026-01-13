/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.sound;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

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
        this.musicFrequency = client.options.getMusicFrequency().getValue();
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
            if (MusicTracker.shouldReplace(musicSound, this.current)) {
                this.client.getSoundManager().stop(this.current);
                this.timeUntilNextSong = MathHelper.nextInt(this.random, 0, musicSound.minDelay() / 2);
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
        return newSound.replaceCurrentMusic() && !newSound.sound().value().id().equals(currentSound.getId());
    }

    public void play(MusicSound sound) {
        SoundEvent soundEvent = sound.sound().value();
        this.current = PositionedSoundInstance.music(soundEvent);
        switch (this.client.getSoundManager().play(this.current)) {
            case STARTED: {
                this.client.getToastManager().onMusicTrackStart();
                this.shownToast = true;
                break;
            }
            case STARTED_SILENTLY: {
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
            this.volume += MathHelper.clamp(this.volume, 5.0E-4f, 0.005f);
            if (this.volume > volume) {
                this.volume = volume;
            }
        } else {
            this.volume = 0.03f * volume + 0.97f * this.volume;
            if (Math.abs(this.volume - volume) < 1.0E-4f || this.volume < volume) {
                this.volume = volume;
            }
        }
        this.volume = MathHelper.clamp(this.volume, 0.0f, 1.0f);
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
        return type.sound().value().id().equals(this.current.getId());
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

    @Environment(value=EnvType.CLIENT)
    public static final class MusicFrequency
    extends Enum<MusicFrequency>
    implements StringIdentifiable {
        public static final /* enum */ MusicFrequency DEFAULT = new MusicFrequency("DEFAULT", "options.music_frequency.default", 20);
        public static final /* enum */ MusicFrequency FREQUENT = new MusicFrequency("FREQUENT", "options.music_frequency.frequent", 10);
        public static final /* enum */ MusicFrequency CONSTANT = new MusicFrequency("CONSTANT", "options.music_frequency.constant", 0);
        public static final Codec<MusicFrequency> CODEC;
        private final String name;
        private final int delayBetweenTracks;
        private final Text text;
        private static final /* synthetic */ MusicFrequency[] field_60805;

        public static MusicFrequency[] values() {
            return (MusicFrequency[])field_60805.clone();
        }

        public static MusicFrequency valueOf(String string) {
            return Enum.valueOf(MusicFrequency.class, string);
        }

        private MusicFrequency(String name, String translationKey, int minutesBetweenTracks) {
            this.name = name;
            this.delayBetweenTracks = minutesBetweenTracks * 1200;
            this.text = Text.translatable(translationKey);
        }

        int getDelayBeforePlaying(@Nullable MusicSound music, Random random) {
            if (music == null) {
                return this.delayBetweenTracks;
            }
            if (this == CONSTANT) {
                return 100;
            }
            int i = Math.min(music.minDelay(), this.delayBetweenTracks);
            int j = Math.min(music.maxDelay(), this.delayBetweenTracks);
            return MathHelper.nextInt(random, i, j);
        }

        public Text getText() {
            return this.text;
        }

        @Override
        public String asString() {
            return this.name;
        }

        private static /* synthetic */ MusicFrequency[] method_71936() {
            return new MusicFrequency[]{DEFAULT, FREQUENT, CONSTANT};
        }

        static {
            field_60805 = MusicFrequency.method_71936();
            CODEC = StringIdentifiable.createCodec(MusicFrequency::values);
        }
    }
}
