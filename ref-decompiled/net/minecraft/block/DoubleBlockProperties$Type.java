/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

public static final class DoubleBlockProperties.Type
extends Enum<DoubleBlockProperties.Type> {
    public static final /* enum */ DoubleBlockProperties.Type SINGLE = new DoubleBlockProperties.Type();
    public static final /* enum */ DoubleBlockProperties.Type FIRST = new DoubleBlockProperties.Type();
    public static final /* enum */ DoubleBlockProperties.Type SECOND = new DoubleBlockProperties.Type();
    private static final /* synthetic */ DoubleBlockProperties.Type[] field_21786;

    public static DoubleBlockProperties.Type[] values() {
        return (DoubleBlockProperties.Type[])field_21786.clone();
    }

    public static DoubleBlockProperties.Type valueOf(String string) {
        return Enum.valueOf(DoubleBlockProperties.Type.class, string);
    }

    private static /* synthetic */ DoubleBlockProperties.Type[] method_36705() {
        return new DoubleBlockProperties.Type[]{SINGLE, FIRST, SECOND};
    }

    static {
        field_21786 = DoubleBlockProperties.Type.method_36705();
    }
}
