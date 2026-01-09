package net.minecraft.world.level.storage;

import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;

public record ParsedSaveProperties(SaveProperties properties, DimensionOptionsRegistryHolder.DimensionsConfig dimensions) {
   public ParsedSaveProperties(SaveProperties saveProperties, DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig) {
      this.properties = saveProperties;
      this.dimensions = dimensionsConfig;
   }

   public SaveProperties properties() {
      return this.properties;
   }

   public DimensionOptionsRegistryHolder.DimensionsConfig dimensions() {
      return this.dimensions;
   }
}
