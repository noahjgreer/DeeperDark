/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.registry.DynamicRegistryManager;

public class AbstractPropertiesHandler.PropertyAccessor<V>
implements Supplier<V> {
    private final String key;
    private final V value;
    private final Function<V, String> stringifier;

    AbstractPropertiesHandler.PropertyAccessor(String key, V value, Function<V, String> stringifier) {
        this.key = key;
        this.value = value;
        this.stringifier = stringifier;
    }

    @Override
    public V get() {
        return this.value;
    }

    public T set(DynamicRegistryManager registryManager, V value) {
        Properties properties = AbstractPropertiesHandler.this.copyProperties();
        properties.put(this.key, this.stringifier.apply(value));
        return AbstractPropertiesHandler.this.create(registryManager, properties);
    }
}
