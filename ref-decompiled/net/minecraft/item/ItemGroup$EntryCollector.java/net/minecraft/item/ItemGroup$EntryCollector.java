/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.item.ItemGroup;

@FunctionalInterface
public static interface ItemGroup.EntryCollector {
    public void accept(ItemGroup.DisplayContext var1, ItemGroup.Entries var2);
}
