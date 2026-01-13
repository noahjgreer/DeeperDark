/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.telemetry;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public static final class TelemetryEventProperty.ServerType
extends Enum<TelemetryEventProperty.ServerType>
implements StringIdentifiable {
    public static final /* enum */ TelemetryEventProperty.ServerType REALM = new TelemetryEventProperty.ServerType("realm");
    public static final /* enum */ TelemetryEventProperty.ServerType LOCAL = new TelemetryEventProperty.ServerType("local");
    public static final /* enum */ TelemetryEventProperty.ServerType OTHER = new TelemetryEventProperty.ServerType("server");
    public static final Codec<TelemetryEventProperty.ServerType> CODEC;
    private final String id;
    private static final /* synthetic */ TelemetryEventProperty.ServerType[] field_41495;

    public static TelemetryEventProperty.ServerType[] values() {
        return (TelemetryEventProperty.ServerType[])field_41495.clone();
    }

    public static TelemetryEventProperty.ServerType valueOf(String string) {
        return Enum.valueOf(TelemetryEventProperty.ServerType.class, string);
    }

    private TelemetryEventProperty.ServerType(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ TelemetryEventProperty.ServerType[] method_47758() {
        return new TelemetryEventProperty.ServerType[]{REALM, LOCAL, OTHER};
    }

    static {
        field_41495 = TelemetryEventProperty.ServerType.method_47758();
        CODEC = StringIdentifiable.createCodec(TelemetryEventProperty.ServerType::values);
    }
}
