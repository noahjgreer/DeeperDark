/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryOwner;

static class RegistryBuilder.UntaggedDelegatingLookup<T>
extends RegistryBuilder.UntaggedLookup<T>
implements RegistryWrapper.Impl.Delegating<T> {
    private final RegistryWrapper.Impl<T> base;

    RegistryBuilder.UntaggedDelegatingLookup(RegistryEntryOwner<T> entryOwner, RegistryWrapper.Impl<T> base) {
        super(entryOwner);
        this.base = base;
    }

    @Override
    public RegistryWrapper.Impl<T> getBase() {
        return this.base;
    }
}
