/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.registry;

import java.util.function.Supplier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import org.jspecify.annotations.Nullable;

static class RegistryBuilder.LazyReferenceEntry<T>
extends RegistryEntry.Reference<T> {
    @Nullable Supplier<T> supplier;

    protected RegistryBuilder.LazyReferenceEntry(RegistryEntryOwner<T> owner, @Nullable RegistryKey<T> key) {
        super(RegistryEntry.Reference.Type.STAND_ALONE, owner, key, null);
    }

    @Override
    protected void setValue(T value) {
        super.setValue(value);
        this.supplier = null;
    }

    @Override
    public T value() {
        if (this.supplier != null) {
            this.setValue(this.supplier.get());
        }
        return super.value();
    }
}
