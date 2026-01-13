/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.RunArgs;
import net.minecraft.client.realms.RealmsClient;

@Environment(value=EnvType.CLIENT)
static final class MinecraftClient.LoadingContext
extends Record {
    private final RealmsClient realmsClient;
    final RunArgs.QuickPlay quickPlayData;

    MinecraftClient.LoadingContext(RealmsClient realmsClient, RunArgs.QuickPlay quickPlayData) {
        this.realmsClient = realmsClient;
        this.quickPlayData = quickPlayData;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MinecraftClient.LoadingContext.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MinecraftClient.LoadingContext.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MinecraftClient.LoadingContext.class, "realmsClient;quickPlayData", "realmsClient", "quickPlayData"}, this, object);
    }

    public RealmsClient realmsClient() {
        return this.realmsClient;
    }

    public RunArgs.QuickPlay quickPlayData() {
        return this.quickPlayData;
    }
}
