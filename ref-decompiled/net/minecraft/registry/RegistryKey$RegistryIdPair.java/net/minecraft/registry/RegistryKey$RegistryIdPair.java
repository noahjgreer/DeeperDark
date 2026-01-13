/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.util.Identifier;

static final class RegistryKey.RegistryIdPair
extends Record {
    final Identifier registry;
    final Identifier id;

    RegistryKey.RegistryIdPair(Identifier registry, Identifier id) {
        this.registry = registry;
        this.id = id;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RegistryKey.RegistryIdPair.class, "registry;identifier", "registry", "id"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RegistryKey.RegistryIdPair.class, "registry;identifier", "registry", "id"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RegistryKey.RegistryIdPair.class, "registry;identifier", "registry", "id"}, this, object);
    }

    public Identifier registry() {
        return this.registry;
    }

    public Identifier id() {
        return this.id;
    }
}
