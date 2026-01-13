/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;

class RegistryBuilder.1
implements RegistryOps.RegistryInfoGetter {
    RegistryBuilder.1() {
    }

    @Override
    public <T> Optional<RegistryOps.RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
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
        return this.get(registryRef).map(WrapperInfoPair::opsInfo);
    }
}
