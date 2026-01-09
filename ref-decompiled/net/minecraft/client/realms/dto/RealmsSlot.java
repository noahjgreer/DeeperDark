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

@Environment(EnvType.CLIENT)
public final class RealmsSlot implements RealmsSerializable {
   @SerializedName("slotId")
   public int slotId;
   @SerializedName("options")
   @JsonAdapter(OptionsTypeAdapter.class)
   public RealmsWorldOptions options;
   @SerializedName("settings")
   public List settings;

   public RealmsSlot(int slotId, RealmsWorldOptions options, List settings) {
      this.slotId = slotId;
      this.options = options;
      this.settings = settings;
   }

   public static RealmsSlot create(int slotId) {
      return new RealmsSlot(slotId, RealmsWorldOptions.getEmptyDefaults(), List.of(RealmsSettingDto.ofHardcore(false)));
   }

   public RealmsSlot clone() {
      return new RealmsSlot(this.slotId, this.options.clone(), new ArrayList(this.settings));
   }

   public boolean isHardcore() {
      return RealmsSettingDto.isHardcore(this.settings);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }

   @Environment(EnvType.CLIENT)
   private static class OptionsTypeAdapter extends TypeAdapter {
      public void write(JsonWriter jsonWriter, RealmsWorldOptions realmsWorldOptions) throws IOException {
         jsonWriter.jsonValue((new CheckedGson()).toJson((RealmsSerializable)realmsWorldOptions));
      }

      public RealmsWorldOptions read(JsonReader jsonReader) throws IOException {
         String string = jsonReader.nextString();
         return RealmsWorldOptions.fromJson(new CheckedGson(), string);
      }

      // $FF: synthetic method
      public Object read(final JsonReader reader) throws IOException {
         return this.read(reader);
      }

      // $FF: synthetic method
      public void write(final JsonWriter writer, final Object options) throws IOException {
         this.write(writer, (RealmsWorldOptions)options);
      }
   }
}
