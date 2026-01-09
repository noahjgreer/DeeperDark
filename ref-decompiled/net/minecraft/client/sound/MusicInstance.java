package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record MusicInstance(@Nullable MusicSound music, float volume) {
   public MusicInstance(MusicSound music) {
      this(music, 1.0F);
   }

   public MusicInstance(@Nullable MusicSound musicSound, float f) {
      this.music = musicSound;
      this.volume = f;
   }

   public boolean shouldReplace(SoundInstance sound) {
      if (this.music == null) {
         return false;
      } else {
         return this.music.replaceCurrentMusic() && !((SoundEvent)this.music.sound().value()).id().equals(sound.getId());
      }
   }

   @Nullable
   public MusicSound music() {
      return this.music;
   }

   public float volume() {
      return this.volume;
   }
}
