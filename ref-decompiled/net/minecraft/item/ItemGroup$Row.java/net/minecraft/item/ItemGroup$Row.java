/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

public static final class ItemGroup.Row
extends Enum<ItemGroup.Row> {
    public static final /* enum */ ItemGroup.Row TOP = new ItemGroup.Row();
    public static final /* enum */ ItemGroup.Row BOTTOM = new ItemGroup.Row();
    private static final /* synthetic */ ItemGroup.Row[] field_41051;

    public static ItemGroup.Row[] values() {
        return (ItemGroup.Row[])field_41051.clone();
    }

    public static ItemGroup.Row valueOf(String string) {
        return Enum.valueOf(ItemGroup.Row.class, string);
    }

    private static /* synthetic */ ItemGroup.Row[] method_47326() {
        return new ItemGroup.Row[]{TOP, BOTTOM};
    }

    static {
        field_41051 = ItemGroup.Row.method_47326();
    }
}
