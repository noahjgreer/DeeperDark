/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

static final class SnbtParsing.Sign
extends Enum<SnbtParsing.Sign> {
    public static final /* enum */ SnbtParsing.Sign PLUS = new SnbtParsing.Sign();
    public static final /* enum */ SnbtParsing.Sign MINUS = new SnbtParsing.Sign();
    private static final /* synthetic */ SnbtParsing.Sign[] field_58016;

    public static SnbtParsing.Sign[] values() {
        return (SnbtParsing.Sign[])field_58016.clone();
    }

    public static SnbtParsing.Sign valueOf(String string) {
        return Enum.valueOf(SnbtParsing.Sign.class, string);
    }

    public void append(StringBuilder builder) {
        if (this == MINUS) {
            builder.append("-");
        }
    }

    private static /* synthetic */ SnbtParsing.Sign[] method_68653() {
        return new SnbtParsing.Sign[]{PLUS, MINUS};
    }

    static {
        field_58016 = SnbtParsing.Sign.method_68653();
    }
}
