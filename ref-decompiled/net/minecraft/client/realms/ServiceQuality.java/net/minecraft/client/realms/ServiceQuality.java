/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public final class ServiceQuality
extends Enum<ServiceQuality> {
    public static final /* enum */ ServiceQuality GREAT = new ServiceQuality(1, "icon/ping_5");
    public static final /* enum */ ServiceQuality GOOD = new ServiceQuality(2, "icon/ping_4");
    public static final /* enum */ ServiceQuality OKAY = new ServiceQuality(3, "icon/ping_3");
    public static final /* enum */ ServiceQuality POOR = new ServiceQuality(4, "icon/ping_2");
    public static final /* enum */ ServiceQuality UNKNOWN = new ServiceQuality(5, "icon/ping_unknown");
    final int index;
    private final Identifier icon;
    private static final /* synthetic */ ServiceQuality[] field_60240;

    public static ServiceQuality[] values() {
        return (ServiceQuality[])field_60240.clone();
    }

    public static ServiceQuality valueOf(String string) {
        return Enum.valueOf(ServiceQuality.class, string);
    }

    private ServiceQuality(int index, String icon) {
        this.index = index;
        this.icon = Identifier.ofVanilla(icon);
    }

    public static @Nullable ServiceQuality byIndex(int index) {
        for (ServiceQuality serviceQuality : ServiceQuality.values()) {
            if (serviceQuality.getIndex() != index) continue;
            return serviceQuality;
        }
        return null;
    }

    public int getIndex() {
        return this.index;
    }

    public Identifier getIcon() {
        return this.icon;
    }

    private static /* synthetic */ ServiceQuality[] method_71196() {
        return new ServiceQuality[]{GREAT, GOOD, OKAY, POOR, UNKNOWN};
    }

    static {
        field_60240 = ServiceQuality.method_71196();
    }

    @Environment(value=EnvType.CLIENT)
    public static class ServiceQualityTypeAdapter
    extends TypeAdapter<ServiceQuality> {
        private static final Logger LOGGER = LogUtils.getLogger();

        public void write(JsonWriter jsonWriter, ServiceQuality serviceQuality) throws IOException {
            jsonWriter.value((long)serviceQuality.index);
        }

        public ServiceQuality read(JsonReader jsonReader) throws IOException {
            int i = jsonReader.nextInt();
            ServiceQuality serviceQuality = ServiceQuality.byIndex(i);
            if (serviceQuality == null) {
                LOGGER.warn("Unsupported ServiceQuality {}", (Object)i);
                return UNKNOWN;
            }
            return serviceQuality;
        }

        public /* synthetic */ Object read(JsonReader reader) throws IOException {
            return this.read(reader);
        }

        public /* synthetic */ void write(JsonWriter writer, Object serviceQuality) throws IOException {
            this.write(writer, (ServiceQuality)((Object)serviceQuality));
        }
    }
}
