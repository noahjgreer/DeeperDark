/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate;

import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.predicate.FluidPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.entry.RegistryEntryList;

public static class FluidPredicate.Builder {
    private Optional<RegistryEntryList<Fluid>> tag = Optional.empty();
    private Optional<StatePredicate> state = Optional.empty();

    private FluidPredicate.Builder() {
    }

    public static FluidPredicate.Builder create() {
        return new FluidPredicate.Builder();
    }

    public FluidPredicate.Builder fluid(Fluid fluid) {
        this.tag = Optional.of(RegistryEntryList.of(fluid.getRegistryEntry()));
        return this;
    }

    public FluidPredicate.Builder tag(RegistryEntryList<Fluid> tag) {
        this.tag = Optional.of(tag);
        return this;
    }

    public FluidPredicate.Builder state(StatePredicate state) {
        this.state = Optional.of(state);
        return this;
    }

    public FluidPredicate build() {
        return new FluidPredicate(this.tag, this.state);
    }
}
