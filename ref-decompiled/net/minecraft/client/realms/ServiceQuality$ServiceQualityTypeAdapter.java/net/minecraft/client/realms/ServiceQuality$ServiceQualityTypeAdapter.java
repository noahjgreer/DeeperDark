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
import net.minecraft.client.realms.ServiceQuality;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public static class ServiceQuality.ServiceQualityTypeAdapter
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
