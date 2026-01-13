/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.telemetry.PropertyMap
 *  net.minecraft.client.session.telemetry.PropertyMap$Builder
 *  net.minecraft.client.session.telemetry.TelemetryEventProperty
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session.telemetry;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PropertyMap {
    final Map<TelemetryEventProperty<?>, Object> backingMap;

    PropertyMap(Map<TelemetryEventProperty<?>, Object> backingMap) {
        this.backingMap = backingMap;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static MapCodec<PropertyMap> createCodec(List<TelemetryEventProperty<?>> properties) {
        return new /* Unavailable Anonymous Inner Class!! */;
    }

    public <T> @Nullable T get(TelemetryEventProperty<T> property) {
        return (T)this.backingMap.get(property);
    }

    public String toString() {
        return this.backingMap.toString();
    }

    public Set<TelemetryEventProperty<?>> keySet() {
        return this.backingMap.keySet();
    }
}

