/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Unit;

class SimpleResourceReload.1
implements ResourceReloader.Synchronizer {
    final /* synthetic */ Executor field_18050;
    final /* synthetic */ ResourceReloader field_18051;
    final /* synthetic */ CompletableFuture field_18052;

    SimpleResourceReload.1() {
        this.field_18050 = executor;
        this.field_18051 = resourceReloader;
        this.field_18052 = completableFuture;
    }

    @Override
    public <T> CompletableFuture<T> whenPrepared(T preparedObject) {
        this.field_18050.execute(() -> {
            SimpleResourceReload.this.waitingReloaders.remove(this.field_18051);
            if (SimpleResourceReload.this.waitingReloaders.isEmpty()) {
                SimpleResourceReload.this.prepareStageFuture.complete(Unit.INSTANCE);
            }
        });
        return SimpleResourceReload.this.prepareStageFuture.thenCombine((CompletionStage)this.field_18052, (unit, object2) -> preparedObject);
    }
}
