/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 */
package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;

public static interface RegistryWrapper.Impl<T>
extends RegistryWrapper<T>,
RegistryEntryOwner<T> {
    public RegistryKey<? extends Registry<? extends T>> getKey();

    public Lifecycle getLifecycle();

    default public RegistryWrapper.Impl<T> withFeatureFilter(FeatureSet enabledFeatures) {
        if (ToggleableFeature.FEATURE_ENABLED_REGISTRY_KEYS.contains(this.getKey())) {
            return this.withPredicateFilter(feature -> ((ToggleableFeature)feature).isEnabled(enabledFeatures));
        }
        return this;
    }

    default public RegistryWrapper.Impl<T> withPredicateFilter(final Predicate<T> predicate) {
        return new Delegating<T>(){

            @Override
            public RegistryWrapper.Impl<T> getBase() {
                return this;
            }

            @Override
            public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
                return this.getBase().getOptional(key).filter(entry -> predicate.test(entry.value()));
            }

            @Override
            public Stream<RegistryEntry.Reference<T>> streamEntries() {
                return this.getBase().streamEntries().filter(entry -> predicate.test(entry.value()));
            }
        };
    }

    public static interface Delegating<T>
    extends RegistryWrapper.Impl<T> {
        public RegistryWrapper.Impl<T> getBase();

        @Override
        default public RegistryKey<? extends Registry<? extends T>> getKey() {
            return this.getBase().getKey();
        }

        @Override
        default public Lifecycle getLifecycle() {
            return this.getBase().getLifecycle();
        }

        @Override
        default public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
            return this.getBase().getOptional(key);
        }

        @Override
        default public Stream<RegistryEntry.Reference<T>> streamEntries() {
            return this.getBase().streamEntries();
        }

        @Override
        default public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
            return this.getBase().getOptional(tag);
        }

        @Override
        default public Stream<RegistryEntryList.Named<T>> getTags() {
            return this.getBase().getTags();
        }
    }
}
