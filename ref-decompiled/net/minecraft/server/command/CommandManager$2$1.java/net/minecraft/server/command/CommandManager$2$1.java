/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import java.util.Optional;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;

class CommandManager.1
implements RegistryWrapper.Impl.Delegating<T> {
    final /* synthetic */ RegistryWrapper.Impl field_40922;

    CommandManager.1(CommandManager.2 arg, RegistryWrapper.Impl impl) {
        this.field_40922 = impl;
    }

    @Override
    public RegistryWrapper.Impl<T> getBase() {
        return this.field_40922;
    }

    @Override
    public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
        return Optional.of(this.getOrThrow(tag));
    }

    @Override
    public RegistryEntryList.Named<T> getOrThrow(TagKey<T> tag) {
        Optional<RegistryEntryList.Named<RegistryEntryList.Named>> optional = this.getBase().getOptional(tag);
        return optional.orElseGet(() -> RegistryEntryList.of(this.getBase(), tag));
    }
}
