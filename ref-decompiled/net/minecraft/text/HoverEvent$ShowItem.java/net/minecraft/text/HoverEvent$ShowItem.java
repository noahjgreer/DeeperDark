/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.text;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;

public record HoverEvent.ShowItem(ItemStack item) implements HoverEvent
{
    public static final MapCodec<HoverEvent.ShowItem> CODEC = ItemStack.MAP_CODEC.xmap(HoverEvent.ShowItem::new, HoverEvent.ShowItem::item);

    public HoverEvent.ShowItem(ItemStack stack) {
        this.item = stack = stack.copy();
    }

    @Override
    public HoverEvent.Action getAction() {
        return HoverEvent.Action.SHOW_ITEM;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HoverEvent.ShowItem)) return false;
        HoverEvent.ShowItem showItem = (HoverEvent.ShowItem)o;
        if (!ItemStack.areEqual(this.item, showItem.item)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return ItemStack.hashCode(this.item);
    }
}
