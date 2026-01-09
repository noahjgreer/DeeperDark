package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;

@Environment(EnvType.CLIENT)
public record RealmsSettingDto(String name, String value) implements RealmsSerializable {
   public RealmsSettingDto(String string, String string2) {
      this.name = string;
      this.value = string2;
   }

   public static RealmsSettingDto ofHardcore(boolean hardcore) {
      return new RealmsSettingDto("hardcore", Boolean.toString(hardcore));
   }

   public static boolean isHardcore(List settings) {
      Iterator var1 = settings.iterator();

      RealmsSettingDto realmsSettingDto;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         realmsSettingDto = (RealmsSettingDto)var1.next();
      } while(!realmsSettingDto.name().equals("hardcore"));

      return Boolean.parseBoolean(realmsSettingDto.value());
   }

   @SerializedName("name")
   public String name() {
      return this.name;
   }

   @SerializedName("value")
   public String value() {
      return this.value;
   }
}
