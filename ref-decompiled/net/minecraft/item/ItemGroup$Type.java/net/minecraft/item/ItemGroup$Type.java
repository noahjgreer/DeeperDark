/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

public static final class ItemGroup.Type
extends Enum<ItemGroup.Type> {
    public static final /* enum */ ItemGroup.Type CATEGORY = new ItemGroup.Type();
    public static final /* enum */ ItemGroup.Type INVENTORY = new ItemGroup.Type();
    public static final /* enum */ ItemGroup.Type HOTBAR = new ItemGroup.Type();
    public static final /* enum */ ItemGroup.Type SEARCH = new ItemGroup.Type();
    private static final /* synthetic */ ItemGroup.Type[] field_41056;

    public static ItemGroup.Type[] values() {
        return (ItemGroup.Type[])field_41056.clone();
    }

    public static ItemGroup.Type valueOf(String string) {
        return Enum.valueOf(ItemGroup.Type.class, string);
    }

    private static /* synthetic */ ItemGroup.Type[] method_47327() {
        return new ItemGroup.Type[]{CATEGORY, INVENTORY, HOTBAR, SEARCH};
    }

    static {
        field_41056 = ItemGroup.Type.method_47327();
    }
}
