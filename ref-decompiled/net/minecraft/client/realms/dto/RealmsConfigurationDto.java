package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record RealmsConfigurationDto(RealmsOptionsDto options, List settings, @Nullable RealmsRegionSelectionPreference regionSelectionPreference, @Nullable RealmsDescriptionDto description) implements RealmsSerializable {
   public RealmsConfigurationDto(RealmsOptionsDto realmsOptionsDto, List list, @Nullable RealmsRegionSelectionPreference realmsRegionSelectionPreference, @Nullable RealmsDescriptionDto realmsDescriptionDto) {
      this.options = realmsOptionsDto;
      this.settings = list;
      this.regionSelectionPreference = realmsRegionSelectionPreference;
      this.description = realmsDescriptionDto;
   }

   @SerializedName("options")
   public RealmsOptionsDto options() {
      return this.options;
   }

   @SerializedName("settings")
   public List settings() {
      return this.settings;
   }

   @Nullable
   @SerializedName("regionSelectionPreference")
   public RealmsRegionSelectionPreference regionSelectionPreference() {
      return this.regionSelectionPreference;
   }

   @Nullable
   @SerializedName("description")
   public RealmsDescriptionDto description() {
      return this.description;
   }
}
