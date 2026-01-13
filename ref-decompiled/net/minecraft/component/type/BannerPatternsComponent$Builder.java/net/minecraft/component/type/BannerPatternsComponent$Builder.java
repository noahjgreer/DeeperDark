/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package net.minecraft.component.type;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;

public static class BannerPatternsComponent.Builder {
    private final ImmutableList.Builder<BannerPatternsComponent.Layer> entries = ImmutableList.builder();

    @Deprecated
    public BannerPatternsComponent.Builder add(RegistryEntryLookup<BannerPattern> patternLookup, RegistryKey<BannerPattern> pattern, DyeColor color) {
        Optional<RegistryEntry.Reference<BannerPattern>> optional = patternLookup.getOptional(pattern);
        if (optional.isEmpty()) {
            LOGGER.warn("Unable to find banner pattern with id: '{}'", (Object)pattern.getValue());
            return this;
        }
        return this.add((RegistryEntry<BannerPattern>)optional.get(), color);
    }

    public BannerPatternsComponent.Builder add(RegistryEntry<BannerPattern> pattern, DyeColor color) {
        return this.add(new BannerPatternsComponent.Layer(pattern, color));
    }

    public BannerPatternsComponent.Builder add(BannerPatternsComponent.Layer layer) {
        this.entries.add((Object)layer);
        return this;
    }

    public BannerPatternsComponent.Builder addAll(BannerPatternsComponent patterns) {
        this.entries.addAll(patterns.layers);
        return this;
    }

    public BannerPatternsComponent build() {
        return new BannerPatternsComponent((List<BannerPatternsComponent.Layer>)this.entries.build());
    }
}
