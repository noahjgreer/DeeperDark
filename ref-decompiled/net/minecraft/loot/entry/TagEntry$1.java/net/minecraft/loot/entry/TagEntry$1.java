/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.entry;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.registry.entry.RegistryEntry;

class TagEntry.1
extends LeafEntry.Choice {
    final /* synthetic */ RegistryEntry field_1007;

    TagEntry.1(TagEntry tagEntry, RegistryEntry registryEntry) {
        this.field_1007 = registryEntry;
        super(tagEntry);
    }

    @Override
    public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
        lootConsumer.accept(new ItemStack(this.field_1007));
    }
}
