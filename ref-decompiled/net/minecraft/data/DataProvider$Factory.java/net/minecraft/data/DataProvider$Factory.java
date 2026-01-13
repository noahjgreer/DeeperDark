/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;

@FunctionalInterface
public static interface DataProvider.Factory<T extends DataProvider> {
    public T create(DataOutput var1);
}
