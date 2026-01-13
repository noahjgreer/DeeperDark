/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;

class RegistryWrapper.Impl.1
implements RegistryWrapper.Impl.Delegating<T> {
    final /* synthetic */ Predicate field_40931;

    RegistryWrapper.Impl.1(Predicate predicate) {
        this.field_40931 = predicate;
    }

    @Override
    public RegistryWrapper.Impl<T> getBase() {
        return Impl.this;
    }

    @Override
    public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
        return this.getBase().getOptional(key).filter(entry -> this.field_40931.test(entry.value()));
    }

    @Override
    public Stream<RegistryEntry.Reference<T>> streamEntries() {
        return this.getBase().streamEntries().filter(entry -> this.field_40931.test(entry.value()));
    }
}
