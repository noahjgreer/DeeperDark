/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.slot;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.slot.ItemStream;

public record ItemStream.Map(ItemStream slots, Function<ItemStack, ? extends ItemStream> mapper) implements ItemStream
{
    @Override
    public Stream<ItemStack> itemCopies() {
        return this.slots.itemCopies().map(this.mapper).flatMap(ItemStream::itemCopies);
    }
}
