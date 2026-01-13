/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record CustomPayload.Type<B extends PacketByteBuf, T extends CustomPayload>(CustomPayload.Id<T> id, PacketCodec<B, T> codec) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{CustomPayload.Type.class, "type;codec", "id", "codec"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CustomPayload.Type.class, "type;codec", "id", "codec"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CustomPayload.Type.class, "type;codec", "id", "codec"}, this, object);
    }
}
