/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.DataPackContents;

static final class MinecraftServer.ResourceManagerHolder
extends Record
implements AutoCloseable {
    final LifecycledResourceManager resourceManager;
    final DataPackContents dataPackContents;

    MinecraftServer.ResourceManagerHolder(LifecycledResourceManager resourceManager, DataPackContents dataPackContents) {
        this.resourceManager = resourceManager;
        this.dataPackContents = dataPackContents;
    }

    @Override
    public void close() {
        this.resourceManager.close();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MinecraftServer.ResourceManagerHolder.class, "resourceManager;managers", "resourceManager", "dataPackContents"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MinecraftServer.ResourceManagerHolder.class, "resourceManager;managers", "resourceManager", "dataPackContents"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MinecraftServer.ResourceManagerHolder.class, "resourceManager;managers", "resourceManager", "dataPackContents"}, this, object);
    }

    public LifecycledResourceManager resourceManager() {
        return this.resourceManager;
    }

    public DataPackContents dataPackContents() {
        return this.dataPackContents;
    }
}
