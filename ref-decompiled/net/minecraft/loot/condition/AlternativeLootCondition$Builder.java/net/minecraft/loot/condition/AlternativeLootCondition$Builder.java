/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.loot.condition;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.loot.condition.LootCondition;

public static abstract class AlternativeLootCondition.Builder
implements LootCondition.Builder {
    private final ImmutableList.Builder<LootCondition> terms = ImmutableList.builder();

    protected AlternativeLootCondition.Builder(LootCondition.Builder ... terms) {
        for (LootCondition.Builder builder : terms) {
            this.terms.add((Object)builder.build());
        }
    }

    public void add(LootCondition.Builder builder) {
        this.terms.add((Object)builder.build());
    }

    @Override
    public LootCondition build() {
        return this.build((List<LootCondition>)this.terms.build());
    }

    protected abstract LootCondition build(List<LootCondition> var1);
}
