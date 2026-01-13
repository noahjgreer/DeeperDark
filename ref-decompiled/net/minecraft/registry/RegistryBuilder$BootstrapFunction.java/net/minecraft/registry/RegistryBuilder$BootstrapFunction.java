/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.registry.Registerable;

@FunctionalInterface
public static interface RegistryBuilder.BootstrapFunction<T> {
    public void run(Registerable<T> var1);
}
