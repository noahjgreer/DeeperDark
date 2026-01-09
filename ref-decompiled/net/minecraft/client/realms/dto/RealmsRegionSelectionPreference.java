package net.minecraft.client.realms.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsRegionSelectionPreference extends ValueObject implements RealmsSerializable {
   public static final RealmsRegionSelectionPreference DEFAULT;
   private static final Logger LOGGER;
   @SerializedName("regionSelectionPreference")
   @JsonAdapter(RegionSelectionMethod.SelectionMethodTypeAdapter.class)
   public RegionSelectionMethod selectionMethod;
   @SerializedName("preferredRegion")
   @JsonAdapter(RealmsRegion.RegionTypeAdapter.class)
   @Nullable
   public RealmsRegion preferredRegion;

   public RealmsRegionSelectionPreference(RegionSelectionMethod selectionMethod, @Nullable RealmsRegion preferredRegion) {
      this.selectionMethod = selectionMethod;
      this.preferredRegion = preferredRegion;
   }

   private RealmsRegionSelectionPreference() {
   }

   public static RealmsRegionSelectionPreference parse(CheckedGson gson, String json) {
      try {
         RealmsRegionSelectionPreference realmsRegionSelectionPreference = (RealmsRegionSelectionPreference)gson.fromJson(json, RealmsRegionSelectionPreference.class);
         if (realmsRegionSelectionPreference == null) {
            LOGGER.error("Could not parse RegionSelectionPreference: {}", json);
            return new RealmsRegionSelectionPreference();
         } else {
            return realmsRegionSelectionPreference;
         }
      } catch (Exception var3) {
         LOGGER.error("Could not parse RegionSelectionPreference: {}", var3.getMessage());
         return new RealmsRegionSelectionPreference();
      }
   }

   public RealmsRegionSelectionPreference clone() {
      return new RealmsRegionSelectionPreference(this.selectionMethod, this.preferredRegion);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }

   static {
      DEFAULT = new RealmsRegionSelectionPreference(RegionSelectionMethod.AUTOMATIC_OWNER, (RealmsRegion)null);
      LOGGER = LogUtils.getLogger();
   }
}
