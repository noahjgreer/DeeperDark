package net.minecraft.sound;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.util.Identifier;

public record SoundEvent(Identifier id, Optional fixedRange) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("sound_id").forGetter(SoundEvent::id), Codec.FLOAT.lenientOptionalFieldOf("range").forGetter(SoundEvent::fixedRange)).apply(instance, SoundEvent::of);
   });
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec PACKET_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   public SoundEvent(Identifier id, Optional optional) {
      this.id = id;
      this.fixedRange = optional;
   }

   private static SoundEvent of(Identifier id, Optional fixedRange) {
      return (SoundEvent)fixedRange.map((fixedRangex) -> {
         return of(id, fixedRangex);
      }).orElseGet(() -> {
         return of(id);
      });
   }

   public static SoundEvent of(Identifier id) {
      return new SoundEvent(id, Optional.empty());
   }

   public static SoundEvent of(Identifier id, float fixedRange) {
      return new SoundEvent(id, Optional.of(fixedRange));
   }

   public float getDistanceToTravel(float volume) {
      return (Float)this.fixedRange.orElse(volume > 1.0F ? 16.0F * volume : 16.0F);
   }

   public Identifier id() {
      return this.id;
   }

   public Optional fixedRange() {
      return this.fixedRange;
   }

   static {
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.SOUND_EVENT, CODEC);
      PACKET_CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, SoundEvent::id, PacketCodecs.FLOAT.collect(PacketCodecs::optional), SoundEvent::fixedRange, SoundEvent::of);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.SOUND_EVENT, PACKET_CODEC);
   }
}
