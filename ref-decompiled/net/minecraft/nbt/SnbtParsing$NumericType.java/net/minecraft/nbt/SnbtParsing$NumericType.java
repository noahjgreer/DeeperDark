/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt;

static final class SnbtParsing.NumericType
extends Enum<SnbtParsing.NumericType> {
    public static final /* enum */ SnbtParsing.NumericType FLOAT = new SnbtParsing.NumericType();
    public static final /* enum */ SnbtParsing.NumericType DOUBLE = new SnbtParsing.NumericType();
    public static final /* enum */ SnbtParsing.NumericType BYTE = new SnbtParsing.NumericType();
    public static final /* enum */ SnbtParsing.NumericType SHORT = new SnbtParsing.NumericType();
    public static final /* enum */ SnbtParsing.NumericType INT = new SnbtParsing.NumericType();
    public static final /* enum */ SnbtParsing.NumericType LONG = new SnbtParsing.NumericType();
    private static final /* synthetic */ SnbtParsing.NumericType[] field_58026;

    public static SnbtParsing.NumericType[] values() {
        return (SnbtParsing.NumericType[])field_58026.clone();
    }

    public static SnbtParsing.NumericType valueOf(String string) {
        return Enum.valueOf(SnbtParsing.NumericType.class, string);
    }

    private static /* synthetic */ SnbtParsing.NumericType[] method_68656() {
        return new SnbtParsing.NumericType[]{FLOAT, DOUBLE, BYTE, SHORT, INT, LONG};
    }

    static {
        field_58026 = SnbtParsing.NumericType.method_68656();
    }
}
