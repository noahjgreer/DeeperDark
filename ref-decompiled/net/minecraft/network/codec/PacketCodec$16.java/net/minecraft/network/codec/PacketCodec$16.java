/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

class PacketCodec.16
implements PacketCodec<B, U> {
    final /* synthetic */ Function field_48592;
    final /* synthetic */ Function field_48593;

    PacketCodec.16(Function function, Function function2) {
        this.field_48592 = function;
        this.field_48593 = function2;
    }

    @Override
    public U decode(B object) {
        Object object2 = PacketCodec.this.decode(object);
        PacketCodec packetCodec = (PacketCodec)this.field_48592.apply(object2);
        return packetCodec.decode(object);
    }

    @Override
    public void encode(B object, U object2) {
        Object object3 = this.field_48593.apply(object2);
        PacketCodec packetCodec = (PacketCodec)this.field_48592.apply(object3);
        PacketCodec.this.encode(object, object3);
        packetCodec.encode(object, object2);
    }
}
