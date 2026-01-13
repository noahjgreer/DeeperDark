/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.registry.Registry;

@FunctionalInterface
static interface Registries.Initializer<T> {
    public Object run(Registry<T> var1);
}
