/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 */
package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;

public record SaveLoading.DataPacks(ResourcePackManager manager, DataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
    public Pair<DataConfiguration, LifecycledResourceManager> load() {
        DataConfiguration dataConfiguration = MinecraftServer.loadDataPacks(this.manager, this.initialDataConfig, this.initMode, this.safeMode);
        List<ResourcePack> list = this.manager.createResourcePacks();
        LifecycledResourceManagerImpl lifecycledResourceManager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, list);
        return Pair.of((Object)dataConfiguration, (Object)lifecycledResourceManager);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SaveLoading.DataPacks.class, "packRepository;initialDataConfig;safeMode;initMode", "manager", "initialDataConfig", "safeMode", "initMode"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SaveLoading.DataPacks.class, "packRepository;initialDataConfig;safeMode;initMode", "manager", "initialDataConfig", "safeMode", "initMode"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SaveLoading.DataPacks.class, "packRepository;initialDataConfig;safeMode;initMode", "manager", "initialDataConfig", "safeMode", "initMode"}, this, object);
    }
}
