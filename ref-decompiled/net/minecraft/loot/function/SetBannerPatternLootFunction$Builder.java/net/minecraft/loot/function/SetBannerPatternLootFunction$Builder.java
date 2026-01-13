/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetBannerPatternLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;

public static class SetBannerPatternLootFunction.Builder
extends ConditionalLootFunction.Builder<SetBannerPatternLootFunction.Builder> {
    private final BannerPatternsComponent.Builder patterns = new BannerPatternsComponent.Builder();
    private final boolean append;

    SetBannerPatternLootFunction.Builder(boolean append) {
        this.append = append;
    }

    @Override
    protected SetBannerPatternLootFunction.Builder getThisBuilder() {
        return this;
    }

    @Override
    public LootFunction build() {
        return new SetBannerPatternLootFunction(this.getConditions(), this.patterns.build(), this.append);
    }

    public SetBannerPatternLootFunction.Builder pattern(RegistryEntry<BannerPattern> pattern, DyeColor color) {
        this.patterns.add(pattern, color);
        return this;
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
