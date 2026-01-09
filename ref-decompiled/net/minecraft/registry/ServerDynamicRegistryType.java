package net.minecraft.registry;

import java.util.List;

public enum ServerDynamicRegistryType {
   STATIC,
   WORLDGEN,
   DIMENSIONS,
   RELOADABLE;

   private static final List VALUES = List.of(values());
   private static final DynamicRegistryManager.Immutable STATIC_REGISTRY_MANAGER = DynamicRegistryManager.of(Registries.REGISTRIES);

   public static CombinedDynamicRegistries createCombinedDynamicRegistries() {
      return (new CombinedDynamicRegistries(VALUES)).with(STATIC, (DynamicRegistryManager.Immutable[])(STATIC_REGISTRY_MANAGER));
   }

   // $FF: synthetic method
   private static ServerDynamicRegistryType[] method_45140() {
      return new ServerDynamicRegistryType[]{STATIC, WORLDGEN, DIMENSIONS, RELOADABLE};
   }
}
