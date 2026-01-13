/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.jukebox.JukeboxSong
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.JukeboxPlayableComponent
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.RegistryByteBuf
 *  net.minecraft.network.codec.PacketCodec
 *  net.minecraft.network.codec.PacketCodecs
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.RegistryWrapper$WrapperLookup
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.entry.RegistryFixedCodec
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextCodecs
 *  net.minecraft.util.dynamic.Codecs
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.block.jukebox;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record JukeboxSong(RegistryEntry<SoundEvent> soundEvent, Text description, float lengthInSeconds, int comparatorOutput) {
    private final RegistryEntry<SoundEvent> soundEvent;
    private final Text description;
    private final float lengthInSeconds;
    private final int comparatorOutput;
    public static final Codec<JukeboxSong> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)SoundEvent.ENTRY_CODEC.fieldOf("sound_event").forGetter(JukeboxSong::soundEvent), (App)TextCodecs.CODEC.fieldOf("description").forGetter(JukeboxSong::description), (App)Codecs.POSITIVE_FLOAT.fieldOf("length_in_seconds").forGetter(JukeboxSong::lengthInSeconds), (App)Codecs.rangedInt((int)0, (int)15).fieldOf("comparator_output").forGetter(JukeboxSong::comparatorOutput)).apply((Applicative)instance, JukeboxSong::new));
    public static final PacketCodec<RegistryByteBuf, JukeboxSong> PACKET_CODEC = PacketCodec.tuple((PacketCodec)SoundEvent.ENTRY_PACKET_CODEC, JukeboxSong::soundEvent, (PacketCodec)TextCodecs.REGISTRY_PACKET_CODEC, JukeboxSong::description, (PacketCodec)PacketCodecs.FLOAT, JukeboxSong::lengthInSeconds, (PacketCodec)PacketCodecs.VAR_INT, JukeboxSong::comparatorOutput, JukeboxSong::new);
    public static final Codec<RegistryEntry<JukeboxSong>> ENTRY_CODEC = RegistryFixedCodec.of((RegistryKey)RegistryKeys.JUKEBOX_SONG);
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<JukeboxSong>> ENTRY_PACKET_CODEC = PacketCodecs.registryEntry((RegistryKey)RegistryKeys.JUKEBOX_SONG, (PacketCodec)PACKET_CODEC);
    private static final int TICKS_PER_SECOND = 20;

    public JukeboxSong(RegistryEntry<SoundEvent> soundEvent, Text description, float lengthInSeconds, int comparatorOutput) {
        this.soundEvent = soundEvent;
        this.description = description;
        this.lengthInSeconds = lengthInSeconds;
        this.comparatorOutput = comparatorOutput;
    }

    public int getLengthInTicks() {
        return MathHelper.ceil((float)(this.lengthInSeconds * 20.0f));
    }

    public boolean shouldStopPlaying(long ticksSinceSongStarted) {
        return ticksSinceSongStarted >= (long)(this.getLengthInTicks() + 20);
    }

    public static Optional<RegistryEntry<JukeboxSong>> getSongEntryFromStack(RegistryWrapper.WrapperLookup registries, ItemStack stack) {
        JukeboxPlayableComponent jukeboxPlayableComponent = (JukeboxPlayableComponent)stack.get(DataComponentTypes.JUKEBOX_PLAYABLE);
        if (jukeboxPlayableComponent != null) {
            return jukeboxPlayableComponent.song().resolveEntry(registries);
        }
        return Optional.empty();
    }

    public RegistryEntry<SoundEvent> soundEvent() {
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
}

