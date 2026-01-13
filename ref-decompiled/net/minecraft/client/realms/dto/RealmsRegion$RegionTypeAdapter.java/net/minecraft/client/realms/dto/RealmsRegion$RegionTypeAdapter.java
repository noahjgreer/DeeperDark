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
package net.minecraft.client.realms.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsRegion;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public static class RealmsRegion.RegionTypeAdapter
extends TypeAdapter<RealmsRegion> {
    private static final Logger LOGGER = LogUtils.getLogger();

    public void write(JsonWriter jsonWriter, RealmsRegion realmsRegion) throws IOException {
        jsonWriter.value(realmsRegion.name);
    }

    public RealmsRegion read(JsonReader jsonReader) throws IOException {
        String string = jsonReader.nextString();
        RealmsRegion realmsRegion = RealmsRegion.fromName(string);
        if (realmsRegion == null) {
            LOGGER.warn("Unsupported RealmsRegion {}", (Object)string);
            return INVALID_REGION;
        }
        return realmsRegion;
    }

    public /* synthetic */ Object read(JsonReader reader) throws IOException {
        return this.read(reader);
    }

    public /* synthetic */ void write(JsonWriter writer, Object region) throws IOException {
        this.write(writer, (RealmsRegion)((Object)region));
    }
}
