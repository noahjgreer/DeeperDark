package net.minecraft.data.tag.vanilla;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.ValueLookupTagProvider;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FluidTags;

public class VanillaFluidTagProvider extends ValueLookupTagProvider {
   public VanillaFluidTagProvider(DataOutput output, CompletableFuture registriesFuture) {
      super(output, RegistryKeys.FLUID, registriesFuture, (fluid) -> {
         return fluid.getRegistryEntry().registryKey();
      });
   }

   protected void configure(RegistryWrapper.WrapperLookup registries) {
      this.builder(FluidTags.WATER).add((Object[])(Fluids.WATER, Fluids.FLOWING_WATER));
      this.builder(FluidTags.LAVA).add((Object[])(Fluids.LAVA, Fluids.FLOWING_LAVA));
   }
}
