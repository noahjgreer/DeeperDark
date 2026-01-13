/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;

static class PacketCodec.1
implements PacketCodec<B, V> {
    final /* synthetic */ PacketDecoder field_48582;
    final /* synthetic */ PacketEncoder field_48583;

    PacketCodec.1(PacketDecoder packetDecoder, PacketEncoder packetEncoder) {
        this.field_48582 = packetDecoder;
        this.field_48583 = packetEncoder;
    }

    @Override
    public V decode(B object) {
        return this.field_48582.decode(object);
    }

    @Override
    public void encode(B object, V object2) {
        this.field_48583.encode(object, object2);
    }
}
