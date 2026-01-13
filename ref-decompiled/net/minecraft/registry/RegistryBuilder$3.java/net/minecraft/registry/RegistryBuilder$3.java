/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.registry;

import com.mojang.serialization.DynamicOps;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;

static class RegistryBuilder.3
implements RegistryWrapper.WrapperLookup {
    final /* synthetic */ Map field_49167;

    RegistryBuilder.3(Map map) {
        this.field_49167 = map;
    }

    @Override
    public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
        return this.field_49167.keySet().stream();
    }

    <T> Optional<RegistryBuilder.WrapperInfoPair<T>> get(RegistryKey<? extends Registry<? extends T>> registryRef) {
        final class WrapperInfoPair<T>
        extends Record {
            private final RegistryWrapper.Impl<T> lookup;
            private final RegistryOps.RegistryInfo<T> opsInfo;

            WrapperInfoPair(RegistryWrapper.Impl<T> lookup, RegistryOps.RegistryInfo<T> opsInfo) {
                this.lookup = lookup;
                this.opsInfo = opsInfo;
            }

            public static <T> WrapperInfoPair<T> of(RegistryWrapper.Impl<T> wrapper) {
                return new WrapperInfoPair<T>(new RegistryBuilder.UntaggedDelegatingLookup<T>(wrapper, wrapper), RegistryOps.RegistryInfo.fromWrapper(wrapper));
            }

            public static <T> WrapperInfoPair<T> of(RegistryBuilder.AnyOwner owner, RegistryWrapper.Impl<T> wrapper) {
                return new WrapperInfoPair<T>(new RegistryBuilder.UntaggedDelegatingLookup<T>(owner.downcast(), wrapper), new RegistryOps.RegistryInfo<T>(owner.downcast(), wrapper, wrapper.getLifecycle()));
            }

            public final String toString() {
                return ObjectMethods.bootstrap("toString", new MethodHandle[]{WrapperInfoPair.class, "lookup;opsInfo", "lookup", "opsInfo"}, this);
            }

            public final int hashCode() {
                return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{WrapperInfoPair.class, "lookup;opsInfo", "lookup", "opsInfo"}, this);
            }

            public final boolean equals(Object object) {
                return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{WrapperInfoPair.class, "lookup;opsInfo", "lookup", "opsInfo"}, this, object);
            }

            public RegistryWrapper.Impl<T> lookup() {
                return this.lookup;
            }

            public RegistryOps.RegistryInfo<T> opsInfo() {
                return this.opsInfo;
            }
        }
        return Optional.ofNullable((WrapperInfoPair)this.field_49167.get(registryRef));
    }

    public <T> Optional<RegistryWrapper.Impl<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
        return this.get(registryRef).map(WrapperInfoPair::lookup);
    }

    @Override
    public <V> RegistryOps<V> getOps(DynamicOps<V> delegate) {
        return RegistryOps.of(delegate, new RegistryOps.RegistryInfoGetter(){

            @Override
            public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
                return this.get(registryRef).map(WrapperInfoPair::opsInfo);
            }
        });
    }
}
