/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.dto.RealmsWorldOptions;

@Environment(value=EnvType.CLIENT)
static class RealmsSlot.OptionsTypeAdapter
extends TypeAdapter<RealmsWorldOptions> {
    private RealmsSlot.OptionsTypeAdapter() {
    }

    public void write(JsonWriter jsonWriter, RealmsWorldOptions realmsWorldOptions) throws IOException {
        jsonWriter.jsonValue(new CheckedGson().toJson(realmsWorldOptions));
    }

    public RealmsWorldOptions read(JsonReader jsonReader) throws IOException {
        String string = jsonReader.nextString();
        return RealmsWorldOptions.fromJson(new CheckedGson(), string);
    }

    public /* synthetic */ Object read(JsonReader reader) throws IOException {
        return this.read(reader);
    }

    public /* synthetic */ void write(JsonWriter writer, Object options) throws IOException {
        this.write(writer, (RealmsWorldOptions)options);
    }
}
