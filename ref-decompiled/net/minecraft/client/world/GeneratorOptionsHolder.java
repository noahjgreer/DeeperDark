package net.minecraft.client.world;

import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.InitialWorldOptions;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.server.DataPackContents;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.WorldGenSettings;

@Environment(EnvType.CLIENT)
public record GeneratorOptionsHolder(GeneratorOptions generatorOptions, Registry dimensionOptionsRegistry, DimensionOptionsRegistryHolder selectedDimensions, CombinedDynamicRegistries combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration, InitialWorldOptions initialWorldCreationOptions) {
   public GeneratorOptionsHolder(WorldGenSettings worldGenSettings, CombinedDynamicRegistries combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration) {
      this(worldGenSettings.generatorOptions(), worldGenSettings.dimensionOptionsRegistryHolder(), combinedDynamicRegistries, dataPackContents, dataConfiguration, new InitialWorldOptions(WorldCreator.Mode.SURVIVAL, Set.of(), (RegistryKey)null));
   }

   public GeneratorOptionsHolder(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder selectedDimensions, CombinedDynamicRegistries combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration, InitialWorldOptions initialWorldOptions) {
      this(generatorOptions, combinedDynamicRegistries.get(ServerDynamicRegistryType.DIMENSIONS).getOrThrow(RegistryKeys.DIMENSION), selectedDimensions, combinedDynamicRegistries.with(ServerDynamicRegistryType.DIMENSIONS, (DynamicRegistryManager.Immutable[])()), dataPackContents, dataConfiguration, initialWorldOptions);
   }

   public GeneratorOptionsHolder(GeneratorOptions generatorOptions, Registry registry, DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder, CombinedDynamicRegistries combinedDynamicRegistries, DataPackContents dataPackContents, DataConfiguration dataConfiguration, InitialWorldOptions initialWorldOptions) {
      this.generatorOptions = generatorOptions;
      this.dimensionOptionsRegistry = registry;
      this.selectedDimensions = dimensionOptionsRegistryHolder;
      this.combinedDynamicRegistries = combinedDynamicRegistries;
      this.dataPackContents = dataPackContents;
      this.dataConfiguration = dataConfiguration;
      this.initialWorldCreationOptions = initialWorldOptions;
   }

   public GeneratorOptionsHolder with(GeneratorOptions generatorOptions, DimensionOptionsRegistryHolder selectedDimensions) {
      return new GeneratorOptionsHolder(generatorOptions, this.dimensionOptionsRegistry, selectedDimensions, this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
   }

   public GeneratorOptionsHolder apply(Modifier modifier) {
      return new GeneratorOptionsHolder((GeneratorOptions)modifier.apply(this.generatorOptions), this.dimensionOptionsRegistry, this.selectedDimensions, this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
   }

   public GeneratorOptionsHolder apply(RegistryAwareModifier modifier) {
      return new GeneratorOptionsHolder(this.generatorOptions, this.dimensionOptionsRegistry, (DimensionOptionsRegistryHolder)modifier.apply(this.getCombinedRegistryManager(), this.selectedDimensions), this.combinedDynamicRegistries, this.dataPackContents, this.dataConfiguration, this.initialWorldCreationOptions);
   }

   public DynamicRegistryManager.Immutable getCombinedRegistryManager() {
      return this.combinedDynamicRegistries.getCombinedRegistryManager();
   }

   public void initializeIndexedFeaturesLists() {
      Iterator var1 = this.dimensionOptionsRegistry().iterator();

      while(var1.hasNext()) {
         DimensionOptions dimensionOptions = (DimensionOptions)var1.next();
         dimensionOptions.chunkGenerator().initializeIndexedFeaturesList();
      }

   }

   public GeneratorOptions generatorOptions() {
      return this.generatorOptions;
   }

   public Registry dimensionOptionsRegistry() {
      return this.dimensionOptionsRegistry;
   }

   public DimensionOptionsRegistryHolder selectedDimensions() {
      return this.selectedDimensions;
   }

   public CombinedDynamicRegistries combinedDynamicRegistries() {
      return this.combinedDynamicRegistries;
   }

   public DataPackContents dataPackContents() {
      return this.dataPackContents;
   }

   public DataConfiguration dataConfiguration() {
      return this.dataConfiguration;
   }

   public InitialWorldOptions initialWorldCreationOptions() {
      return this.initialWorldCreationOptions;
   }

   @Environment(EnvType.CLIENT)
   public interface Modifier extends UnaryOperator {
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface RegistryAwareModifier extends BiFunction {
   }
}
