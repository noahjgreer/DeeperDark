/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public static interface ResourceReloader.Synchronizer {
    public <T> CompletableFuture<T> whenPrepared(T var1);
}
