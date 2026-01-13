/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.entry;

import java.util.List;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.LootPoolEntry;

@FunctionalInterface
public static interface CombinedEntry.Factory<T extends CombinedEntry> {
    public T create(List<LootPoolEntry> var1, List<LootCondition> var2);
}
