/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetStewEffectLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.registry.entry.RegistryEntry;

public static class SetStewEffectLootFunction.Builder
extends ConditionalLootFunction.Builder<SetStewEffectLootFunction.Builder> {
    private final ImmutableList.Builder<SetStewEffectLootFunction.StewEffect> map = ImmutableList.builder();

    @Override
    protected SetStewEffectLootFunction.Builder getThisBuilder() {
        return this;
    }

    public SetStewEffectLootFunction.Builder withEffect(RegistryEntry<StatusEffect> effect, LootNumberProvider durationRange) {
        this.map.add((Object)new SetStewEffectLootFunction.StewEffect(effect, durationRange));
        return this;
    }

    @Override
    public LootFunction build() {
        return new SetStewEffectLootFunction(this.getConditions(), (List<SetStewEffectLootFunction.StewEffect>)this.map.build());
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
