/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

static class ComponentType.Builder.SimpleDataComponentType<T>
implements ComponentType<T> {
    private final @Nullable Codec<T> codec;
    private final PacketCodec<? super RegistryByteBuf, T> packetCodec;
    private final boolean skipsHandAnimation;

    ComponentType.Builder.SimpleDataComponentType(@Nullable Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec, boolean skipsHandAnimation) {
        this.codec = codec;
        this.packetCodec = packetCodec;
        this.skipsHandAnimation = skipsHandAnimation;
    }

    @Override
    public boolean skipsHandAnimation() {
        return this.skipsHandAnimation;
    }

    @Override
    public @Nullable Codec<T> getCodec() {
        return this.codec;
    }

    @Override
    public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
        return this.packetCodec;
    }

    public String toString() {
        return Util.registryValueToString(Registries.DATA_COMPONENT_TYPE, this);
    }
}
