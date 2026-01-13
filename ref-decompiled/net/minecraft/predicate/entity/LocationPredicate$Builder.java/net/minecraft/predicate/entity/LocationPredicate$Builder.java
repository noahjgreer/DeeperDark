/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate.entity;

import java.util.Optional;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.LightPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;

public static class LocationPredicate.Builder {
    private NumberRange.DoubleRange x = NumberRange.DoubleRange.ANY;
    private NumberRange.DoubleRange y = NumberRange.DoubleRange.ANY;
    private NumberRange.DoubleRange z = NumberRange.DoubleRange.ANY;
    private Optional<RegistryEntryList<Biome>> biome = Optional.empty();
    private Optional<RegistryEntryList<Structure>> feature = Optional.empty();
    private Optional<RegistryKey<World>> dimension = Optional.empty();
    private Optional<Boolean> smokey = Optional.empty();
    private Optional<LightPredicate> light = Optional.empty();
    private Optional<BlockPredicate> block = Optional.empty();
    private Optional<FluidPredicate> fluid = Optional.empty();
    private Optional<Boolean> canSeeSky = Optional.empty();

    public static LocationPredicate.Builder create() {
        return new LocationPredicate.Builder();
    }

    public static LocationPredicate.Builder createBiome(RegistryEntry<Biome> biome) {
        return LocationPredicate.Builder.create().biome(RegistryEntryList.of(biome));
    }

    public static LocationPredicate.Builder createDimension(RegistryKey<World> dimension) {
        return LocationPredicate.Builder.create().dimension(dimension);
    }

    public static LocationPredicate.Builder createStructure(RegistryEntry<Structure> structure) {
        return LocationPredicate.Builder.create().structure(RegistryEntryList.of(structure));
    }

    public static LocationPredicate.Builder createY(NumberRange.DoubleRange y) {
        return LocationPredicate.Builder.create().y(y);
    }

    public LocationPredicate.Builder x(NumberRange.DoubleRange x) {
        this.x = x;
        return this;
    }

    public LocationPredicate.Builder y(NumberRange.DoubleRange y) {
        this.y = y;
        return this;
    }

    public LocationPredicate.Builder z(NumberRange.DoubleRange z) {
        this.z = z;
        return this;
    }

    public LocationPredicate.Builder biome(RegistryEntryList<Biome> biome) {
        this.biome = Optional.of(biome);
        return this;
    }

    public LocationPredicate.Builder structure(RegistryEntryList<Structure> structure) {
        this.feature = Optional.of(structure);
        return this;
    }

    public LocationPredicate.Builder dimension(RegistryKey<World> dimension) {
        this.dimension = Optional.of(dimension);
        return this;
    }

    public LocationPredicate.Builder light(LightPredicate.Builder light) {
        this.light = Optional.of(light.build());
        return this;
    }

    public LocationPredicate.Builder block(BlockPredicate.Builder block) {
        this.block = Optional.of(block.build());
        return this;
    }

    public LocationPredicate.Builder fluid(FluidPredicate.Builder fluid) {
        this.fluid = Optional.of(fluid.build());
        return this;
    }

    public LocationPredicate.Builder smokey(boolean smokey) {
        this.smokey = Optional.of(smokey);
        return this;
    }

    public LocationPredicate.Builder canSeeSky(boolean canSeeSky) {
        this.canSeeSky = Optional.of(canSeeSky);
        return this;
    }

    public LocationPredicate build() {
        Optional<LocationPredicate.PositionRange> optional = LocationPredicate.PositionRange.create(this.x, this.y, this.z);
        return new LocationPredicate(optional, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid, this.canSeeSky);
    }
}
