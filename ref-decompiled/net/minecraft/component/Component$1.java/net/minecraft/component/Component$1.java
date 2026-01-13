/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component;

import net.minecraft.component.Component;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

class Component.1
implements PacketCodec<RegistryByteBuf, Component<?>> {
    Component.1() {
    }

    @Override
    public Component<?> decode(RegistryByteBuf registryByteBuf) {
        ComponentType componentType = (ComponentType)ComponentType.PACKET_CODEC.decode(registryByteBuf);
        return Component.1.read(registryByteBuf, componentType);
    }

    private static <T> Component<T> read(RegistryByteBuf buf, ComponentType<T> type) {
        return new Component<T>(type, type.getPacketCodec().decode(buf));
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, Component<?> component) {
        Component.1.write(registryByteBuf, component);
    }

    private static <T> void write(RegistryByteBuf buf, Component<T> component) {
        ComponentType.PACKET_CODEC.encode(buf, component.type());
        component.type().getPacketCodec().encode(buf, component.value());
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((RegistryByteBuf)((Object)object), (Component)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((RegistryByteBuf)((Object)object));
    }
}
