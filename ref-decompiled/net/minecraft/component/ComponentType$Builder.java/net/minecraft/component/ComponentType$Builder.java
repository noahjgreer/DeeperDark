/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.component;

import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public static class ComponentType.Builder<T> {
    private @Nullable Codec<T> codec;
    private @Nullable PacketCodec<? super RegistryByteBuf, T> packetCodec;
    private boolean cache;
    private boolean skipsHandAnimation;

    public ComponentType.Builder<T> codec(Codec<T> codec) {
        this.codec = codec;
        return this;
    }

    public ComponentType.Builder<T> packetCodec(PacketCodec<? super RegistryByteBuf, T> packetCodec) {
        this.packetCodec = packetCodec;
        return this;
    }

    public ComponentType.Builder<T> cache() {
        this.cache = true;
        return this;
    }

    public ComponentType<T> build() {
        PacketCodec packetCodec = Objects.requireNonNullElseGet(this.packetCodec, () -> PacketCodecs.registryCodec(Objects.requireNonNull(this.codec, "Missing Codec for component")));
        Codec<T> codec = this.cache && this.codec != null ? DataComponentTypes.CACHE.wrap(this.codec) : this.codec;
        return new SimpleDataComponentType<T>(codec, packetCodec, this.skipsHandAnimation);
    }

    public ComponentType.Builder<T> skipsHandAnimation() {
        this.skipsHandAnimation = true;
        return this;
    }

    static class SimpleDataComponentType<T>
    implements ComponentType<T> {
        private final @Nullable Codec<T> codec;
        private final PacketCodec<? super RegistryByteBuf, T> packetCodec;
        private final boolean skipsHandAnimation;

        SimpleDataComponentType(@Nullable Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec, boolean skipsHandAnimation) {
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
}
