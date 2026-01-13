/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;

public static interface TrackedDataHandler.ImmutableHandler<T>
extends TrackedDataHandler<T> {
    @Override
    default public T copy(T object) {
        return object;
    }
}
