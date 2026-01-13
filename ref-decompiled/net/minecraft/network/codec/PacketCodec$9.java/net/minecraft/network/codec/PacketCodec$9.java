/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function11
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function11;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.9
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_63142;
    final /* synthetic */ PacketCodec field_63143;
    final /* synthetic */ PacketCodec field_63144;
    final /* synthetic */ PacketCodec field_63145;
    final /* synthetic */ PacketCodec field_63146;
    final /* synthetic */ PacketCodec field_63147;
    final /* synthetic */ PacketCodec field_63148;
    final /* synthetic */ PacketCodec field_63149;
    final /* synthetic */ PacketCodec field_63150;
    final /* synthetic */ PacketCodec field_63151;
    final /* synthetic */ PacketCodec field_63152;
    final /* synthetic */ Function11 field_63153;
    final /* synthetic */ Function field_63154;
    final /* synthetic */ Function field_63155;
    final /* synthetic */ Function field_63156;
    final /* synthetic */ Function field_63157;
    final /* synthetic */ Function field_63158;
    final /* synthetic */ Function field_63159;
    final /* synthetic */ Function field_63160;
    final /* synthetic */ Function field_63161;
    final /* synthetic */ Function field_63162;
    final /* synthetic */ Function field_63163;
    final /* synthetic */ Function field_63164;

    PacketCodec.9(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, PacketCodec packetCodec5, PacketCodec packetCodec6, PacketCodec packetCodec7, PacketCodec packetCodec8, PacketCodec packetCodec9, PacketCodec packetCodec10, PacketCodec packetCodec11, Function11 function11, Function function, Function function2, Function function3, Function function4, Function function5, Function function6, Function function7, Function function8, Function function9, Function function10, Function function12) {
        this.field_63142 = packetCodec;
        this.field_63143 = packetCodec2;
        this.field_63144 = packetCodec3;
        this.field_63145 = packetCodec4;
        this.field_63146 = packetCodec5;
        this.field_63147 = packetCodec6;
        this.field_63148 = packetCodec7;
        this.field_63149 = packetCodec8;
        this.field_63150 = packetCodec9;
        this.field_63151 = packetCodec10;
        this.field_63152 = packetCodec11;
        this.field_63153 = function11;
        this.field_63154 = function;
        this.field_63155 = function2;
        this.field_63156 = function3;
        this.field_63157 = function4;
        this.field_63158 = function5;
        this.field_63159 = function6;
        this.field_63160 = function7;
        this.field_63161 = function8;
        this.field_63162 = function9;
        this.field_63163 = function10;
        this.field_63164 = function12;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_63142.decode(object);
        Object object3 = this.field_63143.decode(object);
        Object object4 = this.field_63144.decode(object);
        Object object5 = this.field_63145.decode(object);
        Object object6 = this.field_63146.decode(object);
        Object object7 = this.field_63147.decode(object);
        Object object8 = this.field_63148.decode(object);
        Object object9 = this.field_63149.decode(object);
        Object object10 = this.field_63150.decode(object);
        Object object11 = this.field_63151.decode(object);
        Object object12 = this.field_63152.decode(object);
        return this.field_63153.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11, object12);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_63142.encode(object, this.field_63154.apply(object2));
        this.field_63143.encode(object, this.field_63155.apply(object2));
        this.field_63144.encode(object, this.field_63156.apply(object2));
        this.field_63145.encode(object, this.field_63157.apply(object2));
        this.field_63146.encode(object, this.field_63158.apply(object2));
        this.field_63147.encode(object, this.field_63159.apply(object2));
        this.field_63148.encode(object, this.field_63160.apply(object2));
        this.field_63149.encode(object, this.field_63161.apply(object2));
        this.field_63150.encode(object, this.field_63162.apply(object2));
        this.field_63151.encode(object, this.field_63163.apply(object2));
        this.field_63152.encode(object, this.field_63164.apply(object2));
    }
}
