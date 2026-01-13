/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.enums;

static final class NoteBlockInstrument.Type
extends Enum<NoteBlockInstrument.Type> {
    public static final /* enum */ NoteBlockInstrument.Type BASE_BLOCK = new NoteBlockInstrument.Type();
    public static final /* enum */ NoteBlockInstrument.Type MOB_HEAD = new NoteBlockInstrument.Type();
    public static final /* enum */ NoteBlockInstrument.Type CUSTOM = new NoteBlockInstrument.Type();
    private static final /* synthetic */ NoteBlockInstrument.Type[] field_41609;

    public static NoteBlockInstrument.Type[] values() {
        return (NoteBlockInstrument.Type[])field_41609.clone();
    }

    public static NoteBlockInstrument.Type valueOf(String string) {
        return Enum.valueOf(NoteBlockInstrument.Type.class, string);
    }

    private static /* synthetic */ NoteBlockInstrument.Type[] method_47892() {
        return new NoteBlockInstrument.Type[]{BASE_BLOCK, MOB_HEAD, CUSTOM};
    }

    static {
        field_41609 = NoteBlockInstrument.Type.method_47892();
    }
}
