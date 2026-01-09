package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.ServiceQuality;

@Environment(EnvType.CLIENT)
public record RegionData(RealmsRegion region, ServiceQuality serviceQuality) implements RealmsSerializable {
   public RegionData(RealmsRegion realmsRegion, ServiceQuality serviceQuality) {
      this.region = realmsRegion;
      this.serviceQuality = serviceQuality;
   }

   @SerializedName("regionName")
   public RealmsRegion region() {
      return this.region;
   }

   @SerializedName("serviceQuality")
   public ServiceQuality serviceQuality() {
      return this.serviceQuality;
   }
}
