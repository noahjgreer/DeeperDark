/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.18
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_48598;
    final /* synthetic */ PacketCodec field_48599;
    final /* synthetic */ BiFunction field_48600;
    final /* synthetic */ Function field_48601;
    final /* synthetic */ Function field_48602;

    PacketCodec.18(PacketCodec packetCodec, PacketCodec packetCodec2, BiFunction biFunction, Function function, Function function2) {
        this.field_48598 = packetCodec;
        this.field_48599 = packetCodec2;
        this.field_48600 = biFunction;
        this.field_48601 = function;
        this.field_48602 = function2;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_48598.decode(object);
        Object object3 = this.field_48599.decode(object);
        return this.field_48600.apply(object2, object3);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_48598.encode(object, this.field_48601.apply(object2));
        this.field_48599.encode(object, this.field_48602.apply(object2));
    }
}
