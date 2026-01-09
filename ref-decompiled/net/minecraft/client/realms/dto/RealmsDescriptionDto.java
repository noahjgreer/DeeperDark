package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.RealmsSerializable;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RealmsDescriptionDto extends ValueObject implements RealmsSerializable {
   @SerializedName("name")
   @Nullable
   public String name;
   @SerializedName("description")
   public String description;

   public RealmsDescriptionDto(@Nullable String name, String description) {
      this.name = name;
      this.description = description;
   }
}
