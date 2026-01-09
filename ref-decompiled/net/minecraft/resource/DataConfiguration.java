package net.minecraft.resource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

public record DataConfiguration(DataPackSettings dataPacks, FeatureSet enabledFeatures) {
   public static final String ENABLED_FEATURES_KEY = "enabled_features";
   public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DataPackSettings.CODEC.lenientOptionalFieldOf("DataPacks", DataPackSettings.SAFE_MODE).forGetter(DataConfiguration::dataPacks), FeatureFlags.CODEC.lenientOptionalFieldOf("enabled_features", FeatureFlags.DEFAULT_ENABLED_FEATURES).forGetter(DataConfiguration::enabledFeatures)).apply(instance, DataConfiguration::new);
   });
   public static final Codec CODEC;
   public static final DataConfiguration SAFE_MODE;

   public DataConfiguration(DataPackSettings dataPackSettings, FeatureSet featureSet) {
      this.dataPacks = dataPackSettings;
      this.enabledFeatures = featureSet;
   }

   public DataConfiguration withFeaturesAdded(FeatureSet features) {
      return new DataConfiguration(this.dataPacks, this.enabledFeatures.combine(features));
   }

   public DataPackSettings dataPacks() {
      return this.dataPacks;
   }

   public FeatureSet enabledFeatures() {
      return this.enabledFeatures;
   }

   static {
      CODEC = MAP_CODEC.codec();
      SAFE_MODE = new DataConfiguration(DataPackSettings.SAFE_MODE, FeatureFlags.DEFAULT_ENABLED_FEATURES);
   }
}
