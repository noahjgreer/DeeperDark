package net.minecraft.entity.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.sound.SoundEvent;

public record WolfSoundVariant(RegistryEntry ambientSound, RegistryEntry deathSound, RegistryEntry growlSound, RegistryEntry hurtSound, RegistryEntry pantSound, RegistryEntry whineSound) {
   public static final Codec CODEC = createCodec();
   public static final Codec NETWORK_CODEC = createCodec();
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec PACKET_CODEC;

   public WolfSoundVariant(RegistryEntry registryEntry, RegistryEntry registryEntry2, RegistryEntry registryEntry3, RegistryEntry registryEntry4, RegistryEntry registryEntry5, RegistryEntry registryEntry6) {
      this.ambientSound = registryEntry;
      this.deathSound = registryEntry2;
      this.growlSound = registryEntry3;
      this.hurtSound = registryEntry4;
      this.pantSound = registryEntry5;
      this.whineSound = registryEntry6;
   }

   private static Codec createCodec() {
      return RecordCodecBuilder.create((instance) -> {
         return instance.group(SoundEvent.ENTRY_CODEC.fieldOf("ambient_sound").forGetter(WolfSoundVariant::ambientSound), SoundEvent.ENTRY_CODEC.fieldOf("death_sound").forGetter(WolfSoundVariant::deathSound), SoundEvent.ENTRY_CODEC.fieldOf("growl_sound").forGetter(WolfSoundVariant::growlSound), SoundEvent.ENTRY_CODEC.fieldOf("hurt_sound").forGetter(WolfSoundVariant::hurtSound), SoundEvent.ENTRY_CODEC.fieldOf("pant_sound").forGetter(WolfSoundVariant::pantSound), SoundEvent.ENTRY_CODEC.fieldOf("whine_sound").forGetter(WolfSoundVariant::whineSound)).apply(instance, WolfSoundVariant::new);
      });
   }

   public RegistryEntry ambientSound() {
      return this.ambientSound;
   }

   public RegistryEntry deathSound() {
      return this.deathSound;
   }

   public RegistryEntry growlSound() {
      return this.growlSound;
   }

   public RegistryEntry hurtSound() {
      return this.hurtSound;
   }

   public RegistryEntry pantSound() {
      return this.pantSound;
   }

   public RegistryEntry whineSound() {
      return this.whineSound;
   }

   static {
      ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.WOLF_SOUND_VARIANT);
      PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.WOLF_SOUND_VARIANT);
   }
}
