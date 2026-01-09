package net.minecraft.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;

public record Instrument(RegistryEntry soundEvent, float useDuration, float range, Text description) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(SoundEvent.ENTRY_CODEC.fieldOf("sound_event").forGetter(Instrument::soundEvent), Codecs.POSITIVE_FLOAT.fieldOf("use_duration").forGetter(Instrument::useDuration), Codecs.POSITIVE_FLOAT.fieldOf("range").forGetter(Instrument::range), TextCodecs.CODEC.fieldOf("description").forGetter(Instrument::description)).apply(instance, Instrument::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   public Instrument(RegistryEntry registryEntry, float f, float g, Text text) {
      this.soundEvent = registryEntry;
      this.useDuration = f;
      this.range = g;
      this.description = text;
   }

   public RegistryEntry soundEvent() {
      return this.soundEvent;
   }

   public float useDuration() {
      return this.useDuration;
   }

   public float range() {
      return this.range;
   }

   public Text description() {
      return this.description;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(SoundEvent.ENTRY_PACKET_CODEC, Instrument::soundEvent, PacketCodecs.FLOAT, Instrument::useDuration, PacketCodecs.FLOAT, Instrument::range, TextCodecs.REGISTRY_PACKET_CODEC, Instrument::description, Instrument::new);
      ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.INSTRUMENT, CODEC);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.INSTRUMENT, PACKET_CODEC);
   }
}
