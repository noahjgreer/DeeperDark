/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.nbt.scanner;

public static final class NbtScanner.Result
extends Enum<NbtScanner.Result> {
    public static final /* enum */ NbtScanner.Result CONTINUE = new NbtScanner.Result();
    public static final /* enum */ NbtScanner.Result BREAK = new NbtScanner.Result();
    public static final /* enum */ NbtScanner.Result HALT = new NbtScanner.Result();
    private static final /* synthetic */ NbtScanner.Result[] field_36256;

    public static NbtScanner.Result[] values() {
        return (NbtScanner.Result[])field_36256.clone();
    }

    public static NbtScanner.Result valueOf(String string) {
        return Enum.valueOf(NbtScanner.Result.class, string);
    }

    private static /* synthetic */ NbtScanner.Result[] method_39874() {
        return new NbtScanner.Result[]{CONTINUE, BREAK, HALT};
    }

    static {
        field_36256 = NbtScanner.Result.method_39874();
    }
}
