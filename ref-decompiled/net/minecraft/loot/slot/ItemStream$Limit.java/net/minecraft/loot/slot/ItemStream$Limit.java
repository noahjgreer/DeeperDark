/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.slot;

import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.slot.ItemStream;

public record ItemStream.Limit(ItemStream slots, int limit) implements ItemStream
{
    @Override
    public Stream<ItemStack> itemCopies() {
        return this.slots.itemCopies().limit(this.limit);
    }

    @Override
    public ItemStream limit(int count) {
        return new ItemStream.Limit(this.slots, Math.min(this.limit, count));
    }
}
