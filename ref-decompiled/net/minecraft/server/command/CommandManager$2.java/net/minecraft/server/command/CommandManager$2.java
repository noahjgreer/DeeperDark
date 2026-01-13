/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;

static class CommandManager.2
implements CommandRegistryAccess {
    final /* synthetic */ RegistryWrapper.WrapperLookup field_40921;

    CommandManager.2(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.field_40921 = wrapperLookup;
    }

    @Override
    public FeatureSet getEnabledFeatures() {
        return FeatureFlags.FEATURE_MANAGER.getFeatureSet();
    }

    @Override
    public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
        return this.field_40921.streamAllRegistryKeys();
    }

    public <T> Optional<RegistryWrapper.Impl<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return this.field_40921.getOptional(registryRef).map(this::createTagCreatingLookup);
    }

    private <T> RegistryWrapper.Impl.Delegating<T> createTagCreatingLookup(final RegistryWrapper.Impl<T> original) {
        return new RegistryWrapper.Impl.Delegating<T>(this){

            @Override
            public RegistryWrapper.Impl<T> getBase() {
                return original;
            }

            @Override
            public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
                return Optional.of(this.getOrThrow(tag));
            }

            @Override
            public RegistryEntryList.Named<T> getOrThrow(TagKey<T> tag) {
                Optional<RegistryEntryList.Named<RegistryEntryList.Named>> optional = this.getBase().getOptional(tag);
                return optional.orElseGet(() -> RegistryEntryList.of(this.getBase(), tag));
            }
        };
    }
}
