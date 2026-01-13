/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientDebugSubscriptionManager;
import net.minecraft.world.debug.DebugSubscriptionType;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class ClientDebugSubscriptionManager.TrackableValue<K, V> {
    private final Map<K, ClientDebugSubscriptionManager.ValueWithExpiry<V>> trackableValues = new HashMap<K, ClientDebugSubscriptionManager.ValueWithExpiry<V>>();

    ClientDebugSubscriptionManager.TrackableValue() {
    }

    public void removeAll(Predicate<ClientDebugSubscriptionManager.ValueWithExpiry<V>> predicate) {
        this.trackableValues.values().removeIf(predicate);
    }

    public void removeUUID(K key) {
        this.trackableValues.remove(key);
    }

    public void removeKeys(Predicate<K> predicate) {
        this.trackableValues.keySet().removeIf(predicate);
    }

    public @Nullable V get(K object) {
        ClientDebugSubscriptionManager.ValueWithExpiry<V> valueWithExpiry = this.trackableValues.get(object);
        return valueWithExpiry != null ? (V)valueWithExpiry.value() : null;
    }

    public void apply(long time, K key, DebugSubscriptionType.OptionalValue<V> value) {
        if (value.value().isPresent()) {
            this.trackableValues.put(key, new ClientDebugSubscriptionManager.ValueWithExpiry<V>(value.value().get(), time + (long)value.subscription().getExpiry()));
        } else {
            this.trackableValues.remove(key);
        }
    }

    public void forEach(BiConsumer<K, V> action) {
        this.trackableValues.forEach((? super K k, ? super V v) -> action.accept(k, v.value()));
    }
}
