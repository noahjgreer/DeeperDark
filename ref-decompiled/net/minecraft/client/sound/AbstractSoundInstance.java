package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class AbstractSoundInstance implements SoundInstance {
   @Nullable
   protected Sound sound;
   protected final SoundCategory category;
   protected final Identifier id;
   protected float volume;
   protected float pitch;
   protected double x;
   protected double y;
   protected double z;
   protected boolean repeat;
   protected int repeatDelay;
   protected SoundInstance.AttenuationType attenuationType;
   protected boolean relative;
   protected Random random;

   protected AbstractSoundInstance(SoundEvent sound, SoundCategory category, Random random) {
      this(sound.id(), category, random);
   }

   protected AbstractSoundInstance(Identifier soundId, SoundCategory category, Random random) {
      this.volume = 1.0F;
      this.pitch = 1.0F;
      this.attenuationType = SoundInstance.AttenuationType.LINEAR;
      this.id = soundId;
      this.category = category;
      this.random = random;
   }

   public Identifier getId() {
      return this.id;
   }

   @Nullable
   public WeightedSoundSet getSoundSet(SoundManager soundManager) {
      if (this.id.equals(SoundManager.INTENTIONALLY_EMPTY_ID)) {
         this.sound = SoundManager.INTENTIONALLY_EMPTY_SOUND;
         return SoundManager.INTENTIONALLY_EMPTY_SOUND_SET;
      } else {
         WeightedSoundSet weightedSoundSet = soundManager.get(this.id);
         if (weightedSoundSet == null) {
            this.sound = SoundManager.MISSING_SOUND;
         } else {
            this.sound = weightedSoundSet.getSound(this.random);
         }

         return weightedSoundSet;
      }
   }

   @Nullable
   public Sound getSound() {
      return this.sound;
   }

   public SoundCategory getCategory() {
      return this.category;
   }

   public boolean isRepeatable() {
      return this.repeat;
   }

   public int getRepeatDelay() {
      return this.repeatDelay;
   }

   public float getVolume() {
      return this.volume * this.sound.getVolume().get(this.random);
   }

   public float getPitch() {
      return this.pitch * this.sound.getPitch().get(this.random);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public SoundInstance.AttenuationType getAttenuationType() {
      return this.attenuationType;
   }

   public boolean isRelative() {
      return this.relative;
   }

   public String toString() {
      return "SoundInstance[" + String.valueOf(this.id) + "]";
   }
}
