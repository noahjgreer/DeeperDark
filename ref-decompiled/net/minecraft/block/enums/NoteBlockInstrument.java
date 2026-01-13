/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.enums.NoteBlockInstrument
 *  net.minecraft.block.enums.NoteBlockInstrument$Type
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.StringIdentifiable
 */
package net.minecraft.block.enums;

import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.StringIdentifiable;

/*
 * Exception performing whole class analysis ignored.
 */
public final class NoteBlockInstrument
extends Enum<NoteBlockInstrument>
implements StringIdentifiable {
    public static final /* enum */ NoteBlockInstrument HARP = new NoteBlockInstrument("HARP", 0, "harp", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_HARP, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BASEDRUM = new NoteBlockInstrument("BASEDRUM", 1, "basedrum", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument SNARE = new NoteBlockInstrument("SNARE", 2, "snare", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_SNARE, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument HAT = new NoteBlockInstrument("HAT", 3, "hat", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_HAT, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BASS = new NoteBlockInstrument("BASS", 4, "bass", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_BASS, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument FLUTE = new NoteBlockInstrument("FLUTE", 5, "flute", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BELL = new NoteBlockInstrument("BELL", 6, "bell", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_BELL, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument GUITAR = new NoteBlockInstrument("GUITAR", 7, "guitar", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument CHIME = new NoteBlockInstrument("CHIME", 8, "chime", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_CHIME, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument XYLOPHONE = new NoteBlockInstrument("XYLOPHONE", 9, "xylophone", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument IRON_XYLOPHONE = new NoteBlockInstrument("IRON_XYLOPHONE", 10, "iron_xylophone", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument COW_BELL = new NoteBlockInstrument("COW_BELL", 11, "cow_bell", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument DIDGERIDOO = new NoteBlockInstrument("DIDGERIDOO", 12, "didgeridoo", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BIT = new NoteBlockInstrument("BIT", 13, "bit", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_BIT, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument BANJO = new NoteBlockInstrument("BANJO", 14, "banjo", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_BANJO, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument PLING = new NoteBlockInstrument("PLING", 15, "pling", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_PLING, Type.BASE_BLOCK);
    public static final /* enum */ NoteBlockInstrument ZOMBIE = new NoteBlockInstrument("ZOMBIE", 16, "zombie", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_ZOMBIE, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument SKELETON = new NoteBlockInstrument("SKELETON", 17, "skeleton", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_SKELETON, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument CREEPER = new NoteBlockInstrument("CREEPER", 18, "creeper", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_CREEPER, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument DRAGON = new NoteBlockInstrument("DRAGON", 19, "dragon", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_ENDER_DRAGON, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument WITHER_SKELETON = new NoteBlockInstrument("WITHER_SKELETON", 20, "wither_skeleton", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_WITHER_SKELETON, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument PIGLIN = new NoteBlockInstrument("PIGLIN", 21, "piglin", (RegistryEntry)SoundEvents.BLOCK_NOTE_BLOCK_IMITATE_PIGLIN, Type.MOB_HEAD);
    public static final /* enum */ NoteBlockInstrument CUSTOM_HEAD = new NoteBlockInstrument("CUSTOM_HEAD", 22, "custom_head", (RegistryEntry)SoundEvents.UI_BUTTON_CLICK, Type.CUSTOM);
    private final String name;
    private final RegistryEntry<SoundEvent> sound;
    private final Type type;
    private static final /* synthetic */ NoteBlockInstrument[] field_12652;

    public static NoteBlockInstrument[] values() {
        return (NoteBlockInstrument[])field_12652.clone();
    }

    public static NoteBlockInstrument valueOf(String string) {
        return Enum.valueOf(NoteBlockInstrument.class, string);
    }

    private NoteBlockInstrument(String name, RegistryEntry<SoundEvent> sound, Type type) {
        this.name = name;
        this.sound = sound;
        this.type = type;
    }

    public String asString() {
        return this.name;
    }

    public RegistryEntry<SoundEvent> getSound() {
        return this.sound;
    }

    public boolean canBePitched() {
        return this.type == Type.BASE_BLOCK;
    }

    public boolean hasCustomSound() {
        return this.type == Type.CUSTOM;
    }

    public boolean isNotBaseBlock() {
        return this.type != Type.BASE_BLOCK;
    }

    private static /* synthetic */ NoteBlockInstrument[] method_36730() {
        return new NoteBlockInstrument[]{HARP, BASEDRUM, SNARE, HAT, BASS, FLUTE, BELL, GUITAR, CHIME, XYLOPHONE, IRON_XYLOPHONE, COW_BELL, DIDGERIDOO, BIT, BANJO, PLING, ZOMBIE, SKELETON, CREEPER, DRAGON, WITHER_SKELETON, PIGLIN, CUSTOM_HEAD};
    }

    static {
        field_12652 = NoteBlockInstrument.method_36730();
    }
}

