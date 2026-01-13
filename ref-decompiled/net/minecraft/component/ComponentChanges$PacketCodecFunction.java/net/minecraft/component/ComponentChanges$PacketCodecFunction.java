/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component;

import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@FunctionalInterface
static interface ComponentChanges.PacketCodecFunction {
    public <T> PacketCodec<? super RegistryByteBuf, T> apply(ComponentType<T> var1);
}
