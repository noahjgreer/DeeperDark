/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function7
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function7;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.5
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_54276;
    final /* synthetic */ PacketCodec field_54277;
    final /* synthetic */ PacketCodec field_54278;
    final /* synthetic */ PacketCodec field_54279;
    final /* synthetic */ PacketCodec field_54280;
    final /* synthetic */ PacketCodec field_54281;
    final /* synthetic */ PacketCodec field_54282;
    final /* synthetic */ Function7 field_54283;
    final /* synthetic */ Function field_54284;
    final /* synthetic */ Function field_54285;
    final /* synthetic */ Function field_54286;
    final /* synthetic */ Function field_54287;
    final /* synthetic */ Function field_54288;
    final /* synthetic */ Function field_54289;
    final /* synthetic */ Function field_54290;

    PacketCodec.5(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, PacketCodec packetCodec5, PacketCodec packetCodec6, PacketCodec packetCodec7, Function7 function7, Function function, Function function2, Function function3, Function function4, Function function5, Function function6, Function function8) {
        this.field_54276 = packetCodec;
        this.field_54277 = packetCodec2;
        this.field_54278 = packetCodec3;
        this.field_54279 = packetCodec4;
        this.field_54280 = packetCodec5;
        this.field_54281 = packetCodec6;
        this.field_54282 = packetCodec7;
        this.field_54283 = function7;
        this.field_54284 = function;
        this.field_54285 = function2;
        this.field_54286 = function3;
        this.field_54287 = function4;
        this.field_54288 = function5;
        this.field_54289 = function6;
        this.field_54290 = function8;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_54276.decode(object);
        Object object3 = this.field_54277.decode(object);
        Object object4 = this.field_54278.decode(object);
        Object object5 = this.field_54279.decode(object);
        Object object6 = this.field_54280.decode(object);
        Object object7 = this.field_54281.decode(object);
        Object object8 = this.field_54282.decode(object);
        return this.field_54283.apply(object2, object3, object4, object5, object6, object7, object8);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_54276.encode(object, this.field_54284.apply(object2));
        this.field_54277.encode(object, this.field_54285.apply(object2));
        this.field_54278.encode(object, this.field_54286.apply(object2));
        this.field_54279.encode(object, this.field_54287.apply(object2));
        this.field_54280.encode(object, this.field_54288.apply(object2));
        this.field_54281.encode(object, this.field_54289.apply(object2));
        this.field_54282.encode(object, this.field_54290.apply(object2));
    }
}
