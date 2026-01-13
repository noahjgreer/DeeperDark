/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryEventType;

@Environment(value=EnvType.CLIENT)
public static class TelemetryEventType.Builder {
    private final String id;
    private final String exportKey;
    private final List<TelemetryEventProperty<?>> properties = new ArrayList();
    private boolean optional;

    TelemetryEventType.Builder(String id, String exportKey) {
        this.id = id;
        this.exportKey = exportKey;
    }

    public TelemetryEventType.Builder properties(List<TelemetryEventProperty<?>> properties) {
        this.properties.addAll(properties);
        return this;
    }

    public <T> TelemetryEventType.Builder properties(TelemetryEventProperty<T> property) {
        this.properties.add(property);
        return this;
    }

    public TelemetryEventType.Builder optional() {
        this.optional = true;
        return this;
    }

    public TelemetryEventType build() {
        TelemetryEventType telemetryEventType = new TelemetryEventType(this.id, this.exportKey, List.copyOf(this.properties), this.optional);
        if (TYPES.putIfAbsent(this.id, telemetryEventType) != null) {
            throw new IllegalStateException("Duplicate TelemetryEventType with key: '" + this.id + "'");
        }
        return telemetryEventType;
    }
}
