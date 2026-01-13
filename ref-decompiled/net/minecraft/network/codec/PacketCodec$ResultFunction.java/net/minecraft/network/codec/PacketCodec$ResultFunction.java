/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import net.minecraft.network.codec.PacketCodec;

@FunctionalInterface
public static interface PacketCodec.ResultFunction<B, S, T> {
    public PacketCodec<B, T> apply(PacketCodec<B, S> var1);
}
