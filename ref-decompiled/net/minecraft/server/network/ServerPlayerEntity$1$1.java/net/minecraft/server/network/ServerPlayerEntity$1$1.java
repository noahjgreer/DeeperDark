/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 *  com.google.common.hash.HashCode
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.server.network;

import com.google.common.cache.CacheLoader;
import com.google.common.hash.HashCode;
import com.mojang.serialization.DynamicOps;
import net.minecraft.component.Component;
import net.minecraft.util.dynamic.HashCodeOps;

class ServerPlayerEntity.1
extends CacheLoader<Component<?>, Integer> {
    private final DynamicOps<HashCode> hashOps;

    ServerPlayerEntity.1() {
        this.hashOps = field_58075.getRegistryManager().getOps(HashCodeOps.INSTANCE);
    }

    public Integer load(Component<?> component) {
        return ((HashCode)component.encode(this.hashOps).getOrThrow(error -> new IllegalArgumentException("Failed to hash " + String.valueOf(component) + ": " + error))).asInt();
    }

    public /* synthetic */ Object load(Object component) throws Exception {
        return this.load((Component)component);
    }
}
