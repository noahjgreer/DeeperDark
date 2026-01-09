package net.minecraft.client.realms.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public enum RegionSelectionMethod {
   AUTOMATIC_PLAYER(0, "realms.configuration.region_preference.automatic_player"),
   AUTOMATIC_OWNER(1, "realms.configuration.region_preference.automatic_owner"),
   MANUAL(2, "");

   public static final RegionSelectionMethod DEFAULT = AUTOMATIC_PLAYER;
   public final int index;
   public final String translationKey;

   private RegionSelectionMethod(final int index, final String translationKey) {
      this.index = index;
      this.translationKey = translationKey;
   }

   // $FF: synthetic method
   private static RegionSelectionMethod[] method_71190() {
      return new RegionSelectionMethod[]{AUTOMATIC_PLAYER, AUTOMATIC_OWNER, MANUAL};
   }

   @Environment(EnvType.CLIENT)
   public static class SelectionMethodTypeAdapter extends TypeAdapter {
      private static final Logger LOGGER = LogUtils.getLogger();

      public void write(JsonWriter jsonWriter, RegionSelectionMethod regionSelectionMethod) throws IOException {
         jsonWriter.value((long)regionSelectionMethod.index);
      }

      public RegionSelectionMethod read(JsonReader jsonReader) throws IOException {
         int i = jsonReader.nextInt();
         RegionSelectionMethod[] var3 = RegionSelectionMethod.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            RegionSelectionMethod regionSelectionMethod = var3[var5];
            if (regionSelectionMethod.index == i) {
               return regionSelectionMethod;
            }
         }

         LOGGER.warn("Unsupported RegionSelectionPreference {}", i);
         return RegionSelectionMethod.DEFAULT;
      }

      // $FF: synthetic method
      public Object read(final JsonReader reader) throws IOException {
         return this.read(reader);
      }

      // $FF: synthetic method
      public void write(final JsonWriter writer, final Object selectionMethod) throws IOException {
         this.write(writer, (RegionSelectionMethod)selectionMethod);
      }
   }
}
