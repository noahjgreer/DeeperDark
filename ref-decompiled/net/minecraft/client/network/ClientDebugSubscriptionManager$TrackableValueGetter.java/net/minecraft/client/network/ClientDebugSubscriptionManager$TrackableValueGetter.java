/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDebugSubscriptionManager;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
static interface ClientDebugSubscriptionManager.TrackableValueGetter<K, V> {
    public ClientDebugSubscriptionManager.TrackableValue<K, V> get(ClientDebugSubscriptionManager.TrackableValueMap<V> var1);
}
