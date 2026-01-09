package net.minecraft.client.sound;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MusicTracker {
   private static final int DEFAULT_TIME_UNTIL_NEXT_SONG = 100;
   private final Random random = Random.create();
   private final MinecraftClient client;
   @Nullable
   private SoundInstance current;
   private MusicFrequency musicFrequency;
   private float volume = 1.0F;
   private int timeUntilNextSong = 100;
   private boolean shownToast = false;

   public MusicTracker(MinecraftClient client) {
      this.client = client;
      this.musicFrequency = (MusicFrequency)client.options.getMusicFrequency().getValue();
   }

   public void tick() {
      MusicInstance musicInstance = this.client.getMusicInstance();
      float f = musicInstance.volume();
      if (this.current != null && this.volume != f) {
         boolean bl = this.canFadeTowardsVolume(f);
         if (!bl) {
            return;
         }
      }

      MusicSound musicSound = musicInstance.music();
      if (musicSound == null) {
         this.timeUntilNextSong = Math.max(this.timeUntilNextSong, 100);
      } else {
         if (this.current != null) {
            if (musicInstance.shouldReplace(this.current)) {
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
            this.play(musicInstance);
         }

      }
   }

   public void play(MusicInstance instance) {
      SoundEvent soundEvent = (SoundEvent)instance.music().sound().value();
      this.current = PositionedSoundInstance.music(soundEvent, instance.volume());
      switch (this.client.getSoundManager().play(this.current)) {
         case STARTED:
            this.client.getToastManager().onMusicTrackStart();
            this.shownToast = true;
            break;
         case STARTED_SILENTLY:
            this.shownToast = false;
      }

      this.timeUntilNextSong = Integer.MAX_VALUE;
      this.volume = instance.volume();
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
      } else if (this.volume == volume) {
         return true;
      } else {
         if (this.volume < volume) {
            this.volume += MathHelper.clamp(this.volume, 5.0E-4F, 0.005F);
            if (this.volume > volume) {
               this.volume = volume;
            }
         } else {
            this.volume = 0.03F * volume + 0.97F * this.volume;
            if (Math.abs(this.volume - volume) < 1.0E-4F || this.volume < volume) {
               this.volume = volume;
            }
         }

         this.volume = MathHelper.clamp(this.volume, 0.0F, 1.0F);
         if (this.volume <= 1.0E-4F) {
            this.stop();
            return false;
         } else {
            this.client.getSoundManager().setVolume(this.current, this.volume);
            return true;
         }
      }
   }

   public boolean isPlayingType(MusicSound type) {
      return this.current == null ? false : ((SoundEvent)type.sound().value()).id().equals(this.current.getId());
   }

   @Nullable
   public String getCurrentMusicTranslationKey() {
      if (this.current != null) {
         Sound sound = this.current.getSound();
         if (sound != null) {
            return sound.getIdentifier().toShortTranslationKey();
         }
      }

      return null;
   }

   public void setMusicFrequency(MusicFrequency musicFrequency) {
      this.musicFrequency = musicFrequency;
      this.timeUntilNextSong = this.musicFrequency.getDelayBeforePlaying(this.client.getMusicInstance().music(), this.random);
   }

   @Environment(EnvType.CLIENT)
   public static enum MusicFrequency implements TranslatableOption, StringIdentifiable {
      DEFAULT(20),
      FREQUENT(10),
      CONSTANT(0);

      public static final Codec CODEC = StringIdentifiable.createCodec(MusicFrequency::values);
      private static final String TRANSLATION_KEY_PREFIX = "options.music_frequency.";
      private final int index;
      private final int delayBetweenTracks;
      private final String translationKey;

      private MusicFrequency(final int index) {
         this.index = index;
         this.delayBetweenTracks = index * 1200;
         this.translationKey = "options.music_frequency." + this.name().toLowerCase();
      }

      int getDelayBeforePlaying(@Nullable MusicSound music, Random random) {
         if (music == null) {
            return this.delayBetweenTracks;
         } else if (this == CONSTANT) {
            return 100;
         } else {
            int i = Math.min(music.minDelay(), this.delayBetweenTracks);
            int j = Math.min(music.maxDelay(), this.delayBetweenTracks);
            return MathHelper.nextInt(random, i, j);
         }
      }

      public int getId() {
         return this.index;
      }

      public String getTranslationKey() {
         return this.translationKey;
      }

      public String asString() {
         return this.name();
      }

      // $FF: synthetic method
      private static MusicFrequency[] method_71936() {
         return new MusicFrequency[]{DEFAULT, FREQUENT, CONSTANT};
      }
   }
}
