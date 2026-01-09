package net.minecraft.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record FluidPredicate(Optional fluids, Optional state) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.FLUID).optionalFieldOf("fluids").forGetter(FluidPredicate::fluids), StatePredicate.CODEC.optionalFieldOf("state").forGetter(FluidPredicate::state)).apply(instance, FluidPredicate::new);
   });

   public FluidPredicate(Optional optional, Optional optional2) {
      this.fluids = optional;
      this.state = optional2;
   }

   public boolean test(ServerWorld world, BlockPos pos) {
      if (!world.isPosLoaded(pos)) {
         return false;
      } else {
         FluidState fluidState = world.getFluidState(pos);
         if (this.fluids.isPresent() && !fluidState.isIn((RegistryEntryList)this.fluids.get())) {
            return false;
         } else {
            return !this.state.isPresent() || ((StatePredicate)this.state.get()).test(fluidState);
         }
      }
   }

   public Optional fluids() {
      return this.fluids;
   }

   public Optional state() {
      return this.state;
   }

   public static class Builder {
      private Optional tag = Optional.empty();
      private Optional state = Optional.empty();

      private Builder() {
      }

      public static Builder create() {
         return new Builder();
      }

      public Builder fluid(Fluid fluid) {
         this.tag = Optional.of(RegistryEntryList.of(fluid.getRegistryEntry()));
         return this;
      }

      public Builder tag(RegistryEntryList tag) {
         this.tag = Optional.of(tag);
         return this;
      }

      public Builder state(StatePredicate state) {
         this.state = Optional.of(state);
         return this;
      }

      public FluidPredicate build() {
         return new FluidPredicate(this.tag, this.state);
      }
   }
}
