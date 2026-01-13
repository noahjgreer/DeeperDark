/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.17
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_48595;
    final /* synthetic */ Function field_48596;
    final /* synthetic */ Function field_48597;

    PacketCodec.17(PacketCodec packetCodec, Function function, Function function2) {
        this.field_48595 = packetCodec;
        this.field_48596 = function;
        this.field_48597 = function2;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_48595.decode(object);
        return this.field_48596.apply(object2);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_48595.encode(object, this.field_48597.apply(object2));
    }
}
