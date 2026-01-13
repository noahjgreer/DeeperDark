/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.slot;

import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.slot.ItemStream;

public record ItemStream.Filter(ItemStream slots, Predicate<ItemStack> filter) implements ItemStream
{
    @Override
    public Stream<ItemStack> itemCopies() {
        return this.slots.itemCopies().filter(this.filter);
    }

    @Override
    public ItemStream filter(Predicate<ItemStack> predicate) {
        return new ItemStream.Filter(this.slots, this.filter.and(predicate));
    }
}
