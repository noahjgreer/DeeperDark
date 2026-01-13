/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

public static interface DoubleBlockProperties.PropertyRetriever<S, T> {
    public T getFromBoth(S var1, S var2);

    public T getFrom(S var1);

    public T getFallback();
}
