/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function12
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function12;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.10
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_63117;
    final /* synthetic */ PacketCodec field_63118;
    final /* synthetic */ PacketCodec field_63119;
    final /* synthetic */ PacketCodec field_63120;
    final /* synthetic */ PacketCodec field_63121;
    final /* synthetic */ PacketCodec field_63122;
    final /* synthetic */ PacketCodec field_63123;
    final /* synthetic */ PacketCodec field_63124;
    final /* synthetic */ PacketCodec field_63125;
    final /* synthetic */ PacketCodec field_63126;
    final /* synthetic */ PacketCodec field_63127;
    final /* synthetic */ PacketCodec field_63128;
    final /* synthetic */ Function12 field_63129;
    final /* synthetic */ Function field_63130;
    final /* synthetic */ Function field_63131;
    final /* synthetic */ Function field_63132;
    final /* synthetic */ Function field_63133;
    final /* synthetic */ Function field_63134;
    final /* synthetic */ Function field_63135;
    final /* synthetic */ Function field_63136;
    final /* synthetic */ Function field_63137;
    final /* synthetic */ Function field_63138;
    final /* synthetic */ Function field_63139;
    final /* synthetic */ Function field_63140;
    final /* synthetic */ Function field_63141;

    PacketCodec.10(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, PacketCodec packetCodec5, PacketCodec packetCodec6, PacketCodec packetCodec7, PacketCodec packetCodec8, PacketCodec packetCodec9, PacketCodec packetCodec10, PacketCodec packetCodec11, PacketCodec packetCodec12, Function12 function12, Function function, Function function2, Function function3, Function function4, Function function5, Function function6, Function function7, Function function8, Function function9, Function function10, Function function11, Function function13) {
        this.field_63117 = packetCodec;
        this.field_63118 = packetCodec2;
        this.field_63119 = packetCodec3;
        this.field_63120 = packetCodec4;
        this.field_63121 = packetCodec5;
        this.field_63122 = packetCodec6;
        this.field_63123 = packetCodec7;
        this.field_63124 = packetCodec8;
        this.field_63125 = packetCodec9;
        this.field_63126 = packetCodec10;
        this.field_63127 = packetCodec11;
        this.field_63128 = packetCodec12;
        this.field_63129 = function12;
        this.field_63130 = function;
        this.field_63131 = function2;
        this.field_63132 = function3;
        this.field_63133 = function4;
        this.field_63134 = function5;
        this.field_63135 = function6;
        this.field_63136 = function7;
        this.field_63137 = function8;
        this.field_63138 = function9;
        this.field_63139 = function10;
        this.field_63140 = function11;
        this.field_63141 = function13;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_63117.decode(object);
        Object object3 = this.field_63118.decode(object);
        Object object4 = this.field_63119.decode(object);
        Object object5 = this.field_63120.decode(object);
        Object object6 = this.field_63121.decode(object);
        Object object7 = this.field_63122.decode(object);
        Object object8 = this.field_63123.decode(object);
        Object object9 = this.field_63124.decode(object);
        Object object10 = this.field_63125.decode(object);
        Object object11 = this.field_63126.decode(object);
        Object object12 = this.field_63127.decode(object);
        Object object13 = this.field_63128.decode(object);
        return this.field_63129.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11, object12, object13);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_63117.encode(object, this.field_63130.apply(object2));
        this.field_63118.encode(object, this.field_63131.apply(object2));
        this.field_63119.encode(object, this.field_63132.apply(object2));
        this.field_63120.encode(object, this.field_63133.apply(object2));
        this.field_63121.encode(object, this.field_63134.apply(object2));
        this.field_63122.encode(object, this.field_63135.apply(object2));
        this.field_63123.encode(object, this.field_63136.apply(object2));
        this.field_63124.encode(object, this.field_63137.apply(object2));
        this.field_63125.encode(object, this.field_63138.apply(object2));
        this.field_63126.encode(object, this.field_63139.apply(object2));
        this.field_63127.encode(object, this.field_63140.apply(object2));
        this.field_63128.encode(object, this.field_63141.apply(object2));
    }
}
