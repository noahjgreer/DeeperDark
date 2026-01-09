package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.ServiceQuality;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public record RealmsServerAddress(@Nullable String address, @Nullable String resourcePackUrl, @Nullable String resourcePackHash, @Nullable RegionData regionData) implements RealmsSerializable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RealmsServerAddress NULL = new RealmsServerAddress((String)null, (String)null, (String)null, (RegionData)null);

   public RealmsServerAddress(@Nullable String string, @Nullable String string2, @Nullable String string3, @Nullable RegionData regionData) {
      this.address = string;
      this.resourcePackUrl = string2;
      this.resourcePackHash = string3;
      this.regionData = regionData;
   }

   public static RealmsServerAddress parse(CheckedGson gson, String json) {
      try {
         RealmsServerAddress realmsServerAddress = (RealmsServerAddress)gson.fromJson(json, RealmsServerAddress.class);
         if (realmsServerAddress == null) {
            LOGGER.error("Could not parse RealmsServerAddress: {}", json);
            return NULL;
         } else {
            return realmsServerAddress;
         }
      } catch (Exception var3) {
         LOGGER.error("Could not parse RealmsServerAddress: {}", var3.getMessage());
         return NULL;
      }
   }

   @SerializedName("address")
   @Nullable
   public String address() {
      return this.address;
   }

   @SerializedName("resourcePackUrl")
   @Nullable
   public String resourcePackUrl() {
      return this.resourcePackUrl;
   }

   @SerializedName("resourcePackHash")
   @Nullable
   public String resourcePackHash() {
      return this.resourcePackHash;
   }

   @SerializedName("sessionRegionData")
   @Nullable
   public RegionData regionData() {
      return this.regionData;
   }

   @Environment(EnvType.CLIENT)
   public static record RegionData(@Nullable RealmsRegion region, @Nullable ServiceQuality serviceQuality) implements RealmsSerializable {
      public RegionData(@Nullable RealmsRegion realmsRegion, @Nullable ServiceQuality serviceQuality) {
         this.region = realmsRegion;
         this.serviceQuality = serviceQuality;
      }

      @SerializedName("regionName")
      @Nullable
      public RealmsRegion region() {
         return this.region;
      }

      @SerializedName("serviceQuality")
      @Nullable
      public ServiceQuality serviceQuality() {
         return this.serviceQuality;
      }
   }
}
