/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;

static class CommandRegistryAccess.1
implements CommandRegistryAccess {
    final /* synthetic */ RegistryWrapper.WrapperLookup field_40908;
    final /* synthetic */ FeatureSet field_40909;

    CommandRegistryAccess.1() {
        this.field_40908 = wrapperLookup;
        this.field_40909 = featureSet;
    }

    @Override
    public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
        return this.field_40908.streamAllRegistryKeys();
    }

    public <T> Optional<RegistryWrapper.Impl<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return this.field_40908.getOptional(registryRef).map(wrapper -> wrapper.withFeatureFilter(this.field_40909));
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return this.field_40909;
    }
}
