package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;

public record MusicSound(RegistryEntry sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter((sound) -> {
         return sound.sound;
      }), Codec.INT.fieldOf("min_delay").forGetter((sound) -> {
         return sound.minDelay;
      }), Codec.INT.fieldOf("max_delay").forGetter((sound) -> {
         return sound.maxDelay;
      }), Codec.BOOL.fieldOf("replace_current_music").forGetter((sound) -> {
         return sound.replaceCurrentMusic;
      })).apply(instance, MusicSound::new);
   });

   public MusicSound(RegistryEntry sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
      this.sound = sound;
      this.minDelay = minDelay;
      this.maxDelay = maxDelay;
      this.replaceCurrentMusic = replaceCurrentMusic;
   }

   public RegistryEntry sound() {
      return this.sound;
   }

   public int minDelay() {
      return this.minDelay;
   }

   public int maxDelay() {
      return this.maxDelay;
   }

   public boolean replaceCurrentMusic() {
      return this.replaceCurrentMusic;
   }
}
