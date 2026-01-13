/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataWriter;

@FunctionalInterface
public static interface DataCache.Runner {
    public CompletableFuture<?> update(DataWriter var1);
}
