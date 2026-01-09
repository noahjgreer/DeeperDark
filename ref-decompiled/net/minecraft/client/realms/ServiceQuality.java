package net.minecraft.client.realms;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public enum ServiceQuality {
   GREAT(1, "icon/ping_5"),
   GOOD(2, "icon/ping_4"),
   OKAY(3, "icon/ping_3"),
   POOR(4, "icon/ping_2"),
   UNKNOWN(5, "icon/ping_unknown");

   final int index;
   private final Identifier icon;

   private ServiceQuality(final int index, final String icon) {
      this.index = index;
      this.icon = Identifier.ofVanilla(icon);
   }

   @Nullable
   public static ServiceQuality byIndex(int index) {
      ServiceQuality[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ServiceQuality serviceQuality = var1[var3];
         if (serviceQuality.getIndex() == index) {
            return serviceQuality;
         }
      }

      return null;
   }

   public int getIndex() {
      return this.index;
   }

   public Identifier getIcon() {
      return this.icon;
   }

   // $FF: synthetic method
   private static ServiceQuality[] method_71196() {
      return new ServiceQuality[]{GREAT, GOOD, OKAY, POOR, UNKNOWN};
   }

   @Environment(EnvType.CLIENT)
   public static class ServiceQualityTypeAdapter extends TypeAdapter {
      private static final Logger LOGGER = LogUtils.getLogger();

      public void write(JsonWriter jsonWriter, ServiceQuality serviceQuality) throws IOException {
         jsonWriter.value((long)serviceQuality.index);
      }

      public ServiceQuality read(JsonReader jsonReader) throws IOException {
         int i = jsonReader.nextInt();
         ServiceQuality serviceQuality = ServiceQuality.byIndex(i);
         if (serviceQuality == null) {
            LOGGER.warn("Unsupported ServiceQuality {}", i);
            return ServiceQuality.UNKNOWN;
         } else {
            return serviceQuality;
         }
      }

      // $FF: synthetic method
      public Object read(final JsonReader reader) throws IOException {
         return this.read(reader);
      }

      // $FF: synthetic method
      public void write(final JsonWriter writer, final Object serviceQuality) throws IOException {
         this.write(writer, (ServiceQuality)serviceQuality);
      }
   }
}
