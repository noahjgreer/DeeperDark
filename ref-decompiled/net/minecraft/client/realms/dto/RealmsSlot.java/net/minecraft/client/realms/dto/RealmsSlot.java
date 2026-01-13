/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.TypeAdapter
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.google.gson.stream.JsonReader
 *  com.google.gson.stream.JsonWriter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import net.minecraft.client.realms.dto.RealmsWorldOptions;

@Environment(value=EnvType.CLIENT)
public final class RealmsSlot
implements RealmsSerializable {
    @SerializedName(value="slotId")
    public int slotId;
    @SerializedName(value="options")
    @JsonAdapter(value=OptionsTypeAdapter.class)
    public RealmsWorldOptions options;
    @SerializedName(value="settings")
    public List<RealmsSettingDto> settings;

    public RealmsSlot(int slotId, RealmsWorldOptions options, List<RealmsSettingDto> settings) {
        this.slotId = slotId;
        this.options = options;
        this.settings = settings;
    }

    public static RealmsSlot create(int slotId) {
        return new RealmsSlot(slotId, RealmsWorldOptions.getEmptyDefaults(), List.of(RealmsSettingDto.ofHardcore(false)));
    }

    public RealmsSlot copy() {
        return new RealmsSlot(this.slotId, this.options.copy(), new ArrayList<RealmsSettingDto>(this.settings));
    }

    public boolean isHardcore() {
        return RealmsSettingDto.isHardcore(this.settings);
    }

    @Environment(value=EnvType.CLIENT)
    static class OptionsTypeAdapter
    extends TypeAdapter<RealmsWorldOptions> {
        private OptionsTypeAdapter() {
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
}
