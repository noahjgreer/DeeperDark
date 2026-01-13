/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.function;

public static final class ValueLists.OutOfBoundsHandling
extends Enum<ValueLists.OutOfBoundsHandling> {
    public static final /* enum */ ValueLists.OutOfBoundsHandling ZERO = new ValueLists.OutOfBoundsHandling();
    public static final /* enum */ ValueLists.OutOfBoundsHandling WRAP = new ValueLists.OutOfBoundsHandling();
    public static final /* enum */ ValueLists.OutOfBoundsHandling CLAMP = new ValueLists.OutOfBoundsHandling();
    private static final /* synthetic */ ValueLists.OutOfBoundsHandling[] field_41667;

    public static ValueLists.OutOfBoundsHandling[] values() {
        return (ValueLists.OutOfBoundsHandling[])field_41667.clone();
    }

    public static ValueLists.OutOfBoundsHandling valueOf(String string) {
        return Enum.valueOf(ValueLists.OutOfBoundsHandling.class, string);
    }

    private static /* synthetic */ ValueLists.OutOfBoundsHandling[] method_47919() {
        return new ValueLists.OutOfBoundsHandling[]{ZERO, WRAP, CLAMP};
    }

    static {
        field_41667 = ValueLists.OutOfBoundsHandling.method_47919();
    }
}
