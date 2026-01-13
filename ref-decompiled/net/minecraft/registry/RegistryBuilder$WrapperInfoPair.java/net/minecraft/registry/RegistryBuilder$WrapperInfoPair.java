/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;

record RegistryBuilder.WrapperInfoPair<T>(RegistryWrapper.Impl<T> lookup, RegistryOps.RegistryInfo<T> opsInfo) {
    public static <T> RegistryBuilder.WrapperInfoPair<T> of(RegistryWrapper.Impl<T> wrapper) {
        return new WrapperInfoPair<T>(new RegistryBuilder.UntaggedDelegatingLookup<T>(wrapper, wrapper), RegistryOps.RegistryInfo.fromWrapper(wrapper));
    }

    public static <T> RegistryBuilder.WrapperInfoPair<T> of(RegistryBuilder.AnyOwner owner, RegistryWrapper.Impl<T> wrapper) {
        return new WrapperInfoPair(new RegistryBuilder.UntaggedDelegatingLookup(owner.downcast(), wrapper), new RegistryOps.RegistryInfo(owner.downcast(), wrapper, wrapper.getLifecycle()));
    }
}
