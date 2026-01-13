/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

class ComponentChanges.1
implements ComponentChanges.PacketCodecFunction {
    ComponentChanges.1() {
    }

    public <T> PacketCodec<RegistryByteBuf, T> apply(ComponentType<T> componentType) {
        return componentType.getPacketCodec().cast();
    }
}
