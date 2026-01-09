package net.minecraft.block.jukebox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record JukeboxSong(RegistryEntry soundEvent, Text description, float lengthInSeconds, int comparatorOutput) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(SoundEvent.ENTRY_CODEC.fieldOf("sound_event").forGetter(JukeboxSong::soundEvent), TextCodecs.CODEC.fieldOf("description").forGetter(JukeboxSong::description), Codecs.POSITIVE_FLOAT.fieldOf("length_in_seconds").forGetter(JukeboxSong::lengthInSeconds), Codecs.rangedInt(0, 15).fieldOf("comparator_output").forGetter(JukeboxSong::comparatorOutput)).apply(instance, JukeboxSong::new);
   });
   public static final PacketCodec PACKET_CODEC;
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;
   private static final int TICKS_PER_SECOND = 20;

   public JukeboxSong(RegistryEntry registryEntry, Text text, float f, int i) {
      this.soundEvent = registryEntry;
      this.description = text;
      this.lengthInSeconds = f;
      this.comparatorOutput = i;
   }

   public int getLengthInTicks() {
      return MathHelper.ceil(this.lengthInSeconds * 20.0F);
   }

   public boolean shouldStopPlaying(long ticksSinceSongStarted) {
      return ticksSinceSongStarted >= (long)(this.getLengthInTicks() + 20);
   }

   public static Optional getSongEntryFromStack(RegistryWrapper.WrapperLookup registries, ItemStack stack) {
      JukeboxPlayableComponent jukeboxPlayableComponent = (JukeboxPlayableComponent)stack.get(DataComponentTypes.JUKEBOX_PLAYABLE);
      return jukeboxPlayableComponent != null ? jukeboxPlayableComponent.song().resolveEntry(registries) : Optional.empty();
   }

   public RegistryEntry soundEvent() {
      return this.soundEvent;
   }

   public Text description() {
      return this.description;
   }

   public float lengthInSeconds() {
      return this.lengthInSeconds;
   }

   public int comparatorOutput() {
      return this.comparatorOutput;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(SoundEvent.ENTRY_PACKET_CODEC, JukeboxSong::soundEvent, TextCodecs.REGISTRY_PACKET_CODEC, JukeboxSong::description, PacketCodecs.FLOAT, JukeboxSong::lengthInSeconds, PacketCodecs.VAR_INT, JukeboxSong::comparatorOutput, JukeboxSong::new);
      ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.JUKEBOX_SONG);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.JUKEBOX_SONG, PACKET_CODEC);
   }
}
