/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceReloader;

@FunctionalInterface
protected static interface SimpleResourceReload.Factory<S> {
    public static final SimpleResourceReload.Factory<Void> SIMPLE = (store, reloadSynchronizer, reloader, prepareExecutor, applyExecutor) -> reloader.reload(store, prepareExecutor, reloadSynchronizer, applyExecutor);

    public CompletableFuture<S> create(ResourceReloader.Store var1, ResourceReloader.Synchronizer var2, ResourceReloader var3, Executor var4, Executor var5);
}
