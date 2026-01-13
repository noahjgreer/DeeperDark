/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.jukebox.JukeboxSong
 *  net.minecraft.block.jukebox.JukeboxSongs
 *  net.minecraft.registry.Registerable
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryEntry$Reference
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 */
package net.minecraft.block.jukebox;

import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

/*
 * Exception performing whole class analysis ignored.
 */
public interface JukeboxSongs {
    public static final RegistryKey<JukeboxSong> THIRTEEN = JukeboxSongs.of((String)"13");
    public static final RegistryKey<JukeboxSong> CAT = JukeboxSongs.of((String)"cat");
    public static final RegistryKey<JukeboxSong> BLOCKS = JukeboxSongs.of((String)"blocks");
    public static final RegistryKey<JukeboxSong> CHIRP = JukeboxSongs.of((String)"chirp");
    public static final RegistryKey<JukeboxSong> FAR = JukeboxSongs.of((String)"far");
    public static final RegistryKey<JukeboxSong> MALL = JukeboxSongs.of((String)"mall");
    public static final RegistryKey<JukeboxSong> MELLOHI = JukeboxSongs.of((String)"mellohi");
    public static final RegistryKey<JukeboxSong> STAL = JukeboxSongs.of((String)"stal");
    public static final RegistryKey<JukeboxSong> STRAD = JukeboxSongs.of((String)"strad");
    public static final RegistryKey<JukeboxSong> WARD = JukeboxSongs.of((String)"ward");
    public static final RegistryKey<JukeboxSong> ELEVEN = JukeboxSongs.of((String)"11");
    public static final RegistryKey<JukeboxSong> WAIT = JukeboxSongs.of((String)"wait");
    public static final RegistryKey<JukeboxSong> PIGSTEP = JukeboxSongs.of((String)"pigstep");
    public static final RegistryKey<JukeboxSong> OTHERSIDE = JukeboxSongs.of((String)"otherside");
    public static final RegistryKey<JukeboxSong> FIVE = JukeboxSongs.of((String)"5");
    public static final RegistryKey<JukeboxSong> RELIC = JukeboxSongs.of((String)"relic");
    public static final RegistryKey<JukeboxSong> PRECIPICE = JukeboxSongs.of((String)"precipice");
    public static final RegistryKey<JukeboxSong> CREATOR = JukeboxSongs.of((String)"creator");
    public static final RegistryKey<JukeboxSong> CREATOR_MUSIC_BOX = JukeboxSongs.of((String)"creator_music_box");
    public static final RegistryKey<JukeboxSong> TEARS = JukeboxSongs.of((String)"tears");
    public static final RegistryKey<JukeboxSong> LAVA_CHICKEN = JukeboxSongs.of((String)"lava_chicken");

    private static RegistryKey<JukeboxSong> of(String id) {
        return RegistryKey.of((RegistryKey)RegistryKeys.JUKEBOX_SONG, (Identifier)Identifier.ofVanilla((String)id));
    }

    private static void register(Registerable<JukeboxSong> registry, RegistryKey<JukeboxSong> key, RegistryEntry.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput) {
        registry.register(key, (Object)new JukeboxSong(soundEvent, (Text)Text.translatable((String)Util.createTranslationKey((String)"jukebox_song", (Identifier)key.getValue())), (float)lengthInSeconds, comparatorOutput));
    }

    public static void bootstrap(Registerable<JukeboxSong> registry) {
        JukeboxSongs.register(registry, (RegistryKey)THIRTEEN, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_13, (int)178, (int)1);
        JukeboxSongs.register(registry, (RegistryKey)CAT, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_CAT, (int)185, (int)2);
        JukeboxSongs.register(registry, (RegistryKey)BLOCKS, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_BLOCKS, (int)345, (int)3);
        JukeboxSongs.register(registry, (RegistryKey)CHIRP, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_CHIRP, (int)185, (int)4);
        JukeboxSongs.register(registry, (RegistryKey)FAR, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_FAR, (int)174, (int)5);
        JukeboxSongs.register(registry, (RegistryKey)MALL, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_MALL, (int)197, (int)6);
        JukeboxSongs.register(registry, (RegistryKey)MELLOHI, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_MELLOHI, (int)96, (int)7);
        JukeboxSongs.register(registry, (RegistryKey)STAL, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_STAL, (int)150, (int)8);
        JukeboxSongs.register(registry, (RegistryKey)STRAD, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_STRAD, (int)188, (int)9);
        JukeboxSongs.register(registry, (RegistryKey)WARD, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_WARD, (int)251, (int)10);
        JukeboxSongs.register(registry, (RegistryKey)ELEVEN, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_11, (int)71, (int)11);
        JukeboxSongs.register(registry, (RegistryKey)WAIT, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_WAIT, (int)238, (int)12);
        JukeboxSongs.register(registry, (RegistryKey)PIGSTEP, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_PIGSTEP, (int)149, (int)13);
        JukeboxSongs.register(registry, (RegistryKey)OTHERSIDE, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_OTHERSIDE, (int)195, (int)14);
        JukeboxSongs.register(registry, (RegistryKey)FIVE, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_5, (int)178, (int)15);
        JukeboxSongs.register(registry, (RegistryKey)RELIC, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_RELIC, (int)218, (int)14);
        JukeboxSongs.register(registry, (RegistryKey)PRECIPICE, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_PRECIPICE, (int)299, (int)13);
        JukeboxSongs.register(registry, (RegistryKey)CREATOR, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_CREATOR, (int)176, (int)12);
        JukeboxSongs.register(registry, (RegistryKey)CREATOR_MUSIC_BOX, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_CREATOR_MUSIC_BOX, (int)73, (int)11);
        JukeboxSongs.register(registry, (RegistryKey)TEARS, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_TEARS, (int)175, (int)10);
        JukeboxSongs.register(registry, (RegistryKey)LAVA_CHICKEN, (RegistryEntry.Reference)SoundEvents.MUSIC_DISC_LAVA_CHICKEN, (int)134, (int)9);
    }
}

