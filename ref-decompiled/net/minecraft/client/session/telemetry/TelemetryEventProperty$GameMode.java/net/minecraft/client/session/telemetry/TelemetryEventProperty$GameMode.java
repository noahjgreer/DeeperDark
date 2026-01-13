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
public static final class TelemetryEventProperty.GameMode
extends Enum<TelemetryEventProperty.GameMode>
implements StringIdentifiable {
    public static final /* enum */ TelemetryEventProperty.GameMode SURVIVAL = new TelemetryEventProperty.GameMode("survival", 0);
    public static final /* enum */ TelemetryEventProperty.GameMode CREATIVE = new TelemetryEventProperty.GameMode("creative", 1);
    public static final /* enum */ TelemetryEventProperty.GameMode ADVENTURE = new TelemetryEventProperty.GameMode("adventure", 2);
    public static final /* enum */ TelemetryEventProperty.GameMode SPECTATOR = new TelemetryEventProperty.GameMode("spectator", 6);
    public static final /* enum */ TelemetryEventProperty.GameMode HARDCORE = new TelemetryEventProperty.GameMode("hardcore", 99);
    public static final Codec<TelemetryEventProperty.GameMode> CODEC;
    private final String id;
    private final int rawId;
    private static final /* synthetic */ TelemetryEventProperty.GameMode[] field_41489;

    public static TelemetryEventProperty.GameMode[] values() {
        return (TelemetryEventProperty.GameMode[])field_41489.clone();
    }

    public static TelemetryEventProperty.GameMode valueOf(String string) {
        return Enum.valueOf(TelemetryEventProperty.GameMode.class, string);
    }

    private TelemetryEventProperty.GameMode(String id, int rawId) {
        this.id = id;
        this.rawId = rawId;
    }

    public int getRawId() {
        return this.rawId;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ TelemetryEventProperty.GameMode[] method_47757() {
        return new TelemetryEventProperty.GameMode[]{SURVIVAL, CREATIVE, ADVENTURE, SPECTATOR, HARDCORE};
    }

    static {
        field_41489 = TelemetryEventProperty.GameMode.method_47757();
        CODEC = StringIdentifiable.createCodec(TelemetryEventProperty.GameMode::values);
    }
}
