/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

static final class SnbtParsing.Signedness
extends Enum<SnbtParsing.Signedness> {
    public static final /* enum */ SnbtParsing.Signedness SIGNED = new SnbtParsing.Signedness();
    public static final /* enum */ SnbtParsing.Signedness UNSIGNED = new SnbtParsing.Signedness();
    private static final /* synthetic */ SnbtParsing.Signedness[] field_58019;

    public static SnbtParsing.Signedness[] values() {
        return (SnbtParsing.Signedness[])field_58019.clone();
    }

    public static SnbtParsing.Signedness valueOf(String string) {
        return Enum.valueOf(SnbtParsing.Signedness.class, string);
    }

    private static /* synthetic */ SnbtParsing.Signedness[] method_68655() {
        return new SnbtParsing.Signedness[]{SIGNED, UNSIGNED};
    }

    static {
        field_58019 = SnbtParsing.Signedness.method_68655();
    }
}
