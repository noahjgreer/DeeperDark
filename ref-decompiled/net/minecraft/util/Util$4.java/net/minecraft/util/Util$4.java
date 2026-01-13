/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.jtracy.TracyClient
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import com.mojang.jtracy.TracyClient;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import org.jspecify.annotations.Nullable;

static class Util.4
extends ForkJoinWorkerThread {
    final /* synthetic */ String field_54204;
    final /* synthetic */ String field_54205;

    Util.4(ForkJoinPool forkJoinPool, String string, String string2) {
        this.field_54204 = string;
        this.field_54205 = string2;
        super(forkJoinPool);
    }

    @Override
    protected void onStart() {
        TracyClient.setThreadName((String)this.field_54204, (int)this.field_54205.hashCode());
        super.onStart();
    }

    @Override
    protected void onTermination(@Nullable Throwable throwable) {
        if (throwable != null) {
            LOGGER.warn("{} died", (Object)this.getName(), (Object)throwable);
        } else {
            LOGGER.debug("{} shutdown", (Object)this.getName());
        }
        super.onTermination(throwable);
    }
}
