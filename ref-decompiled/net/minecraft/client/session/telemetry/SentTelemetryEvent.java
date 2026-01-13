/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.TelemetryEvent
 *  com.mojang.authlib.minecraft.TelemetrySession
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.telemetry.PropertyMap
 *  net.minecraft.client.session.telemetry.SentTelemetryEvent
 *  net.minecraft.client.session.telemetry.TelemetryEventType
 */
package net.minecraft.client.session.telemetry;

import com.mojang.authlib.minecraft.TelemetryEvent;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.telemetry.PropertyMap;
import net.minecraft.client.session.telemetry.TelemetryEventType;

@Environment(value=EnvType.CLIENT)
public record SentTelemetryEvent(TelemetryEventType type, PropertyMap properties) {
    private final TelemetryEventType type;
    private final PropertyMap properties;
    public static final Codec<SentTelemetryEvent> CODEC = TelemetryEventType.CODEC.dispatchStable(SentTelemetryEvent::type, TelemetryEventType::getCodec);

    public SentTelemetryEvent(TelemetryEventType type, PropertyMap properties) {
        properties.keySet().forEach(property -> {
            if (!type.hasProperty(property)) {
                throw new IllegalArgumentException("Property '" + property.id() + "' not expected for event: '" + type.getId() + "'");
            }
        });
        this.type = type;
        this.properties = properties;
    }

    public TelemetryEvent createEvent(TelemetrySession session) {
        return this.type.createEvent(session, this.properties);
    }

    public TelemetryEventType type() {
        return this.type;
    }

    public PropertyMap properties() {
        return this.properties;
    }
}

