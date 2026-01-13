/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.function.Consumer;

public static interface DependencyTracker.Dependencies<K> {
    public void forDependencies(Consumer<K> var1);

    public void forOptionalDependencies(Consumer<K> var1);
}
