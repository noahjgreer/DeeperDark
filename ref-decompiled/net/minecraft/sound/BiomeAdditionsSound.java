package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;

public class BiomeAdditionsSound {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(SoundEvent.ENTRY_CODEC.fieldOf("sound").forGetter((sound) -> {
         return sound.sound;
      }), Codec.DOUBLE.fieldOf("tick_chance").forGetter((sound) -> {
         return sound.chance;
      })).apply(instance, BiomeAdditionsSound::new);
   });
   private final RegistryEntry sound;
   private final double chance;

   public BiomeAdditionsSound(RegistryEntry sound, double chance) {
      this.sound = sound;
      this.chance = chance;
   }

   public RegistryEntry getSound() {
      return this.sound;
   }

   public double getChance() {
      return this.chance;
   }
}
