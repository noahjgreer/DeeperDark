/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.decoration;

@FunctionalInterface
public static interface DisplayEntity.AbstractInterpolator<T> {
    public static <T> DisplayEntity.AbstractInterpolator<T> constant(T value) {
        return delta -> value;
    }

    public T interpolate(float var1);
}
