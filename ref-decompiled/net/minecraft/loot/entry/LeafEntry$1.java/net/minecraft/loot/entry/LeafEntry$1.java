/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.entry;

import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;

class LeafEntry.1
extends LeafEntry.Choice {
    LeafEntry.1() {
        super(LeafEntry.this);
    }

    @Override
    public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
        LeafEntry.this.generateLoot(LootFunction.apply(LeafEntry.this.compiledFunctions, lootConsumer, context), context);
    }
}
