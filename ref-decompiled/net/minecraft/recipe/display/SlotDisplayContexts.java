package net.minecraft.recipe.display;

import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.context.ContextType;
import net.minecraft.world.World;

public class SlotDisplayContexts {
   public static final ContextParameter FUEL_REGISTRY = ContextParameter.of("fuel_values");
   public static final ContextParameter REGISTRIES = ContextParameter.of("registries");
   public static final ContextType CONTEXT_TYPE;

   public static ContextParameterMap createParameters(World world) {
      return (new ContextParameterMap.Builder()).add(FUEL_REGISTRY, world.getFuelRegistry()).add(REGISTRIES, world.getRegistryManager()).build(CONTEXT_TYPE);
   }

   static {
      CONTEXT_TYPE = (new ContextType.Builder()).allow(FUEL_REGISTRY).allow(REGISTRIES).build();
   }
}
