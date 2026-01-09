package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.CampfireBlock;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record LocationPredicate(Optional position, Optional biomes, Optional structures, Optional dimension, Optional smokey, Optional light, Optional block, Optional fluid, Optional canSeeSky) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(LocationPredicate.PositionRange.CODEC.optionalFieldOf("position").forGetter(LocationPredicate::position), RegistryCodecs.entryList(RegistryKeys.BIOME).optionalFieldOf("biomes").forGetter(LocationPredicate::biomes), RegistryCodecs.entryList(RegistryKeys.STRUCTURE).optionalFieldOf("structures").forGetter(LocationPredicate::structures), RegistryKey.createCodec(RegistryKeys.WORLD).optionalFieldOf("dimension").forGetter(LocationPredicate::dimension), Codec.BOOL.optionalFieldOf("smokey").forGetter(LocationPredicate::smokey), LightPredicate.CODEC.optionalFieldOf("light").forGetter(LocationPredicate::light), BlockPredicate.CODEC.optionalFieldOf("block").forGetter(LocationPredicate::block), FluidPredicate.CODEC.optionalFieldOf("fluid").forGetter(LocationPredicate::fluid), Codec.BOOL.optionalFieldOf("can_see_sky").forGetter(LocationPredicate::canSeeSky)).apply(instance, LocationPredicate::new);
   });

   public LocationPredicate(Optional optional, Optional optional2, Optional optional3, Optional optional4, Optional optional5, Optional optional6, Optional optional7, Optional optional8, Optional optional9) {
      this.position = optional;
      this.biomes = optional2;
      this.structures = optional3;
      this.dimension = optional4;
      this.smokey = optional5;
      this.light = optional6;
      this.block = optional7;
      this.fluid = optional8;
      this.canSeeSky = optional9;
   }

   public boolean test(ServerWorld world, double x, double y, double z) {
      if (this.position.isPresent() && !((PositionRange)this.position.get()).test(x, y, z)) {
         return false;
      } else if (this.dimension.isPresent() && this.dimension.get() != world.getRegistryKey()) {
         return false;
      } else {
         BlockPos blockPos = BlockPos.ofFloored(x, y, z);
         boolean bl = world.isPosLoaded(blockPos);
         if (this.biomes.isPresent() && (!bl || !((RegistryEntryList)this.biomes.get()).contains(world.getBiome(blockPos)))) {
            return false;
         } else if (this.structures.isPresent() && (!bl || !world.getStructureAccessor().getStructureContaining(blockPos, (RegistryEntryList)this.structures.get()).hasChildren())) {
            return false;
         } else if (!this.smokey.isPresent() || bl && (Boolean)this.smokey.get() == CampfireBlock.isLitCampfireInRange(world, blockPos)) {
            if (this.light.isPresent() && !((LightPredicate)this.light.get()).test(world, blockPos)) {
               return false;
            } else if (this.block.isPresent() && !((BlockPredicate)this.block.get()).test(world, blockPos)) {
               return false;
            } else if (this.fluid.isPresent() && !((FluidPredicate)this.fluid.get()).test(world, blockPos)) {
               return false;
            } else {
               return !this.canSeeSky.isPresent() || (Boolean)this.canSeeSky.get() == world.isSkyVisible(blockPos);
            }
         } else {
            return false;
         }
      }
   }

   public Optional position() {
      return this.position;
   }

   public Optional biomes() {
      return this.biomes;
   }

   public Optional structures() {
      return this.structures;
   }

   public Optional dimension() {
      return this.dimension;
   }

   public Optional smokey() {
      return this.smokey;
   }

   public Optional light() {
      return this.light;
   }

   public Optional block() {
      return this.block;
   }

   public Optional fluid() {
      return this.fluid;
   }

   public Optional canSeeSky() {
      return this.canSeeSky;
   }

   static record PositionRange(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(NumberRange.DoubleRange.CODEC.optionalFieldOf("x", NumberRange.DoubleRange.ANY).forGetter(PositionRange::x), NumberRange.DoubleRange.CODEC.optionalFieldOf("y", NumberRange.DoubleRange.ANY).forGetter(PositionRange::y), NumberRange.DoubleRange.CODEC.optionalFieldOf("z", NumberRange.DoubleRange.ANY).forGetter(PositionRange::z)).apply(instance, PositionRange::new);
      });

      private PositionRange(NumberRange.DoubleRange doubleRange, NumberRange.DoubleRange doubleRange2, NumberRange.DoubleRange doubleRange3) {
         this.x = doubleRange;
         this.y = doubleRange2;
         this.z = doubleRange3;
      }

      static Optional create(NumberRange.DoubleRange x, NumberRange.DoubleRange y, NumberRange.DoubleRange z) {
         return x.isDummy() && y.isDummy() && z.isDummy() ? Optional.empty() : Optional.of(new PositionRange(x, y, z));
      }

      public boolean test(double x, double y, double z) {
         return this.x.test(x) && this.y.test(y) && this.z.test(z);
      }

      public NumberRange.DoubleRange x() {
         return this.x;
      }

      public NumberRange.DoubleRange y() {
         return this.y;
      }

      public NumberRange.DoubleRange z() {
         return this.z;
      }
   }

   public static class Builder {
      private NumberRange.DoubleRange x;
      private NumberRange.DoubleRange y;
      private NumberRange.DoubleRange z;
      private Optional biome;
      private Optional feature;
      private Optional dimension;
      private Optional smokey;
      private Optional light;
      private Optional block;
      private Optional fluid;
      private Optional canSeeSky;

      public Builder() {
         this.x = NumberRange.DoubleRange.ANY;
         this.y = NumberRange.DoubleRange.ANY;
         this.z = NumberRange.DoubleRange.ANY;
         this.biome = Optional.empty();
         this.feature = Optional.empty();
         this.dimension = Optional.empty();
         this.smokey = Optional.empty();
         this.light = Optional.empty();
         this.block = Optional.empty();
         this.fluid = Optional.empty();
         this.canSeeSky = Optional.empty();
      }

      public static Builder create() {
         return new Builder();
      }

      public static Builder createBiome(RegistryEntry biome) {
         return create().biome(RegistryEntryList.of(biome));
      }

      public static Builder createDimension(RegistryKey dimension) {
         return create().dimension(dimension);
      }

      public static Builder createStructure(RegistryEntry structure) {
         return create().structure(RegistryEntryList.of(structure));
      }

      public static Builder createY(NumberRange.DoubleRange y) {
         return create().y(y);
      }

      public Builder x(NumberRange.DoubleRange x) {
         this.x = x;
         return this;
      }

      public Builder y(NumberRange.DoubleRange y) {
         this.y = y;
         return this;
      }

      public Builder z(NumberRange.DoubleRange z) {
         this.z = z;
         return this;
      }

      public Builder biome(RegistryEntryList biome) {
         this.biome = Optional.of(biome);
         return this;
      }

      public Builder structure(RegistryEntryList structure) {
         this.feature = Optional.of(structure);
         return this;
      }

      public Builder dimension(RegistryKey dimension) {
         this.dimension = Optional.of(dimension);
         return this;
      }

      public Builder light(LightPredicate.Builder light) {
         this.light = Optional.of(light.build());
         return this;
      }

      public Builder block(BlockPredicate.Builder block) {
         this.block = Optional.of(block.build());
         return this;
      }

      public Builder fluid(FluidPredicate.Builder fluid) {
         this.fluid = Optional.of(fluid.build());
         return this;
      }

      public Builder smokey(boolean smokey) {
         this.smokey = Optional.of(smokey);
         return this;
      }

      public Builder canSeeSky(boolean canSeeSky) {
         this.canSeeSky = Optional.of(canSeeSky);
         return this;
      }

      public LocationPredicate build() {
         Optional optional = LocationPredicate.PositionRange.create(this.x, this.y, this.z);
         return new LocationPredicate(optional, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid, this.canSeeSky);
      }
   }
}
