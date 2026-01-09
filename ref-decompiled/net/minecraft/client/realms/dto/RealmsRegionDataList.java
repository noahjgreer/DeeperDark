package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;

@Environment(EnvType.CLIENT)
public record RealmsRegionDataList(List regionData) implements RealmsSerializable {
   public RealmsRegionDataList(List list) {
      this.regionData = list;
   }

   public static RealmsRegionDataList empty() {
      return new RealmsRegionDataList(List.of());
   }

   @SerializedName("regionDataList")
   public List regionData() {
      return this.regionData;
   }
}
