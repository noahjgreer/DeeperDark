/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.telemetry;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class PropertyMap.Builder {
    private final Map<TelemetryEventProperty<?>, Object> backingMap = new Reference2ObjectOpenHashMap();

    PropertyMap.Builder() {
    }

    public <T> PropertyMap.Builder put(TelemetryEventProperty<T> property, T value) {
        this.backingMap.put(property, value);
        return this;
    }

    public <T> PropertyMap.Builder putIfNonNull(TelemetryEventProperty<T> property, @Nullable T value) {
        if (value != null) {
            this.backingMap.put(property, value);
        }
        return this;
    }

    public PropertyMap.Builder putAll(PropertyMap map) {
        this.backingMap.putAll(map.backingMap);
        return this;
    }

    public PropertyMap build() {
        return new PropertyMap(this.backingMap);
    }
}
