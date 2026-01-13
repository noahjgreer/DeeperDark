/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.entry;

import java.util.List;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;

@FunctionalInterface
protected static interface LeafEntry.Factory {
    public LeafEntry build(int var1, int var2, List<LootCondition> var3, List<LootFunction> var4);
}
