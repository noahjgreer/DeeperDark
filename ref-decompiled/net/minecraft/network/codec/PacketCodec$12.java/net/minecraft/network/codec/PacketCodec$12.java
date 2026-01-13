/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.ValueFirstEncoder;

static class PacketCodec.12
implements PacketCodec<B, V> {
    final /* synthetic */ PacketDecoder field_48584;
    final /* synthetic */ ValueFirstEncoder field_48585;

    PacketCodec.12(PacketDecoder packetDecoder, ValueFirstEncoder valueFirstEncoder) {
        this.field_48584 = packetDecoder;
        this.field_48585 = valueFirstEncoder;
    }

    @Override
    public V decode(B object) {
        return this.field_48584.decode(object);
    }

    @Override
    public void encode(B object, V object2) {
        this.field_48585.encode(object2, object);
    }
}
