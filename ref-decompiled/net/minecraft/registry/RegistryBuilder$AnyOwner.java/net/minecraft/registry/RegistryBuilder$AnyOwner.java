/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.registry.entry.RegistryEntryOwner;

static class RegistryBuilder.AnyOwner
implements RegistryEntryOwner<Object> {
    RegistryBuilder.AnyOwner() {
    }

    public <T> RegistryEntryOwner<T> downcast() {
        return this;
    }
}
