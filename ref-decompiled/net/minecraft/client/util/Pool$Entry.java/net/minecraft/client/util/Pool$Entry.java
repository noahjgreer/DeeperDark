/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import com.google.common.annotations.VisibleForTesting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;

@Environment(value=EnvType.CLIENT)
@VisibleForTesting
protected static final class Pool.Entry<T>
implements AutoCloseable {
    final ClosableFactory<T> factory;
    final T object;
    int lifespan;

    Pool.Entry(ClosableFactory<T> factory, T object, int lifespan) {
        this.factory = factory;
        this.object = object;
        this.lifespan = lifespan;
    }

    @Override
    public void close() {
        this.factory.close(this.object);
    }
}
