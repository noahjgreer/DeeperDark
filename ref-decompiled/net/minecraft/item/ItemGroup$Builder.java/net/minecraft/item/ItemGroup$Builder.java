/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public static class ItemGroup.Builder {
    private static final ItemGroup.EntryCollector EMPTY_ENTRIES = (displayContext, entries) -> {};
    private final ItemGroup.Row row;
    private final int column;
    private Text displayName = Text.empty();
    private Supplier<ItemStack> iconSupplier = () -> ItemStack.EMPTY;
    private ItemGroup.EntryCollector entryCollector = EMPTY_ENTRIES;
    private boolean scrollbar = true;
    private boolean renderName = true;
    private boolean special = false;
    private ItemGroup.Type type = ItemGroup.Type.CATEGORY;
    private Identifier texture = ITEMS;

    public ItemGroup.Builder(ItemGroup.Row row, int column) {
        this.row = row;
        this.column = column;
    }

    public ItemGroup.Builder displayName(Text displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemGroup.Builder icon(Supplier<ItemStack> iconSupplier) {
        this.iconSupplier = iconSupplier;
        return this;
    }

    public ItemGroup.Builder entries(ItemGroup.EntryCollector entryCollector) {
        this.entryCollector = entryCollector;
        return this;
    }

    public ItemGroup.Builder special() {
        this.special = true;
        return this;
    }

    public ItemGroup.Builder noRenderedName() {
        this.renderName = false;
        return this;
    }

    public ItemGroup.Builder noScrollbar() {
        this.scrollbar = false;
        return this;
    }

    protected ItemGroup.Builder type(ItemGroup.Type type) {
        this.type = type;
        return this;
    }

    public ItemGroup.Builder texture(Identifier texture) {
        this.texture = texture;
        return this;
    }

    public ItemGroup build() {
        if ((this.type == ItemGroup.Type.HOTBAR || this.type == ItemGroup.Type.INVENTORY) && this.entryCollector != EMPTY_ENTRIES) {
            throw new IllegalStateException("Special tabs can't have display items");
        }
        ItemGroup itemGroup = new ItemGroup(this.row, this.column, this.type, this.displayName, this.iconSupplier, this.entryCollector);
        itemGroup.special = this.special;
        itemGroup.renderName = this.renderName;
        itemGroup.scrollbar = this.scrollbar;
        itemGroup.texture = this.texture;
        return itemGroup;
    }
}
