/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.tag;

import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

static class ProvidedTagBuilder.1
implements ProvidedTagBuilder<RegistryKey<T>, T> {
    final /* synthetic */ TagBuilder field_60483;

    ProvidedTagBuilder.1(TagBuilder tagBuilder) {
        this.field_60483 = tagBuilder;
    }

    @Override
    public ProvidedTagBuilder<RegistryKey<T>, T> add(RegistryKey<T> registryKey) {
        this.field_60483.add(registryKey.getValue());
        return this;
    }

    @Override
    public ProvidedTagBuilder<RegistryKey<T>, T> addOptional(RegistryKey<T> registryKey) {
        this.field_60483.addOptional(registryKey.getValue());
        return this;
    }

    @Override
    public ProvidedTagBuilder<RegistryKey<T>, T> addTag(TagKey<T> tag) {
        this.field_60483.addTag(tag.id());
        return this;
    }

    @Override
    public ProvidedTagBuilder<RegistryKey<T>, T> addOptionalTag(TagKey<T> tag) {
        this.field_60483.addOptionalTag(tag.id());
        return this;
    }
}
