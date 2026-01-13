/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

public static final class ItemGroup.StackVisibility
extends Enum<ItemGroup.StackVisibility> {
    public static final /* enum */ ItemGroup.StackVisibility PARENT_AND_SEARCH_TABS = new ItemGroup.StackVisibility();
    public static final /* enum */ ItemGroup.StackVisibility PARENT_TAB_ONLY = new ItemGroup.StackVisibility();
    public static final /* enum */ ItemGroup.StackVisibility SEARCH_TAB_ONLY = new ItemGroup.StackVisibility();
    private static final /* synthetic */ ItemGroup.StackVisibility[] field_40194;

    public static ItemGroup.StackVisibility[] values() {
        return (ItemGroup.StackVisibility[])field_40194.clone();
    }

    public static ItemGroup.StackVisibility valueOf(String string) {
        return Enum.valueOf(ItemGroup.StackVisibility.class, string);
    }

    private static /* synthetic */ ItemGroup.StackVisibility[] method_45425() {
        return new ItemGroup.StackVisibility[]{PARENT_AND_SEARCH_TABS, PARENT_TAB_ONLY, SEARCH_TAB_ONLY};
    }

    static {
        field_40194 = ItemGroup.StackVisibility.method_45425();
    }
}
