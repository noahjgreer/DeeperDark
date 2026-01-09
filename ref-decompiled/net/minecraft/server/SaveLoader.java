package net.minecraft.server;

import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.world.SaveProperties;

public record SaveLoader(LifecycledResourceManager resourceManager, DataPackContents dataPackContents, CombinedDynamicRegistries combinedDynamicRegistries, SaveProperties saveProperties) implements AutoCloseable {
   public SaveLoader(LifecycledResourceManager lifecycledResourceManager, DataPackContents dataPackContents, CombinedDynamicRegistries combinedDynamicRegistries, SaveProperties saveProperties) {
      this.resourceManager = lifecycledResourceManager;
      this.dataPackContents = dataPackContents;
      this.combinedDynamicRegistries = combinedDynamicRegistries;
      this.saveProperties = saveProperties;
   }

   public void close() {
      this.resourceManager.close();
   }

   public LifecycledResourceManager resourceManager() {
      return this.resourceManager;
   }

   public DataPackContents dataPackContents() {
      return this.dataPackContents;
   }

   public CombinedDynamicRegistries combinedDynamicRegistries() {
      return this.combinedDynamicRegistries;
   }

   public SaveProperties saveProperties() {
      return this.saveProperties;
   }
}
