/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

static final class SnbtParsing.Radix
extends Enum<SnbtParsing.Radix> {
    public static final /* enum */ SnbtParsing.Radix BINARY = new SnbtParsing.Radix();
    public static final /* enum */ SnbtParsing.Radix DECIMAL = new SnbtParsing.Radix();
    public static final /* enum */ SnbtParsing.Radix HEX = new SnbtParsing.Radix();
    private static final /* synthetic */ SnbtParsing.Radix[] field_58012;

    public static SnbtParsing.Radix[] values() {
        return (SnbtParsing.Radix[])field_58012.clone();
    }

    public static SnbtParsing.Radix valueOf(String string) {
        return Enum.valueOf(SnbtParsing.Radix.class, string);
    }

    private static /* synthetic */ SnbtParsing.Radix[] method_68648() {
        return new SnbtParsing.Radix[]{BINARY, DECIMAL, HEX};
    }

    static {
        field_58012 = SnbtParsing.Radix.method_68648();
    }
}
