/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 */
package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.SimpleRegistry;

class SimpleRegistry.3
implements Registry.PendingTagLoad<T> {
    final /* synthetic */ Map field_54031;
    final /* synthetic */ RegistryWrapper.Impl field_54030;
    final /* synthetic */ ImmutableMap field_53688;

    SimpleRegistry.3(Map map, RegistryWrapper.Impl impl, ImmutableMap immutableMap) {
        this.field_54031 = map;
        this.field_54030 = impl;
        this.field_53688 = immutableMap;
    }

    @Override
    public RegistryKey<? extends Registry<? extends T>> getKey() {
        return SimpleRegistry.this.getKey();
    }

    @Override
    public int size() {
        return this.field_54031.size();
    }

    @Override
    public RegistryWrapper.Impl<T> getLookup() {
        return this.field_54030;
    }

    @Override
    public void apply() {
        this.field_53688.forEach((tagKey, named) -> {
            List list = this.field_54031.getOrDefault(tagKey, List.of());
            named.setEntries(list);
        });
        SimpleRegistry.this.tagLookup = SimpleRegistry.TagLookup.fromMap(this.field_53688);
        SimpleRegistry.this.refreshTags();
    }
}
