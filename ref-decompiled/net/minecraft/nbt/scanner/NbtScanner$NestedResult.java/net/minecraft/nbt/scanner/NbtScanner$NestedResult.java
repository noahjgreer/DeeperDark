/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt.scanner;

public static final class NbtScanner.NestedResult
extends Enum<NbtScanner.NestedResult> {
    public static final /* enum */ NbtScanner.NestedResult ENTER = new NbtScanner.NestedResult();
    public static final /* enum */ NbtScanner.NestedResult SKIP = new NbtScanner.NestedResult();
    public static final /* enum */ NbtScanner.NestedResult BREAK = new NbtScanner.NestedResult();
    public static final /* enum */ NbtScanner.NestedResult HALT = new NbtScanner.NestedResult();
    private static final /* synthetic */ NbtScanner.NestedResult[] field_36252;

    public static NbtScanner.NestedResult[] values() {
        return (NbtScanner.NestedResult[])field_36252.clone();
    }

    public static NbtScanner.NestedResult valueOf(String string) {
        return Enum.valueOf(NbtScanner.NestedResult.class, string);
    }

    private static /* synthetic */ NbtScanner.NestedResult[] method_39873() {
        return new NbtScanner.NestedResult[]{ENTER, SKIP, BREAK, HALT};
    }

    static {
        field_36252 = NbtScanner.NestedResult.method_39873();
    }
}
