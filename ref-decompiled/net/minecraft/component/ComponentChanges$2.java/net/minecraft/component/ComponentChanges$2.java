/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

class ComponentChanges.2
implements ComponentChanges.PacketCodecFunction {
    ComponentChanges.2() {
    }

    public <T> PacketCodec<RegistryByteBuf, T> apply(ComponentType<T> componentType) {
        PacketCodec packetCodec = componentType.getPacketCodec().cast();
        return packetCodec.collect(PacketCodecs.lengthPrependedRegistry(Integer.MAX_VALUE));
    }
}
