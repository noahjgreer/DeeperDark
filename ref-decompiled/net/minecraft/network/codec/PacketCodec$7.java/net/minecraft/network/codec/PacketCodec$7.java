/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function9
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function9;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.7
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_56222;
    final /* synthetic */ PacketCodec field_56223;
    final /* synthetic */ PacketCodec field_56224;
    final /* synthetic */ PacketCodec field_56225;
    final /* synthetic */ PacketCodec field_56226;
    final /* synthetic */ PacketCodec field_56227;
    final /* synthetic */ PacketCodec field_56228;
    final /* synthetic */ PacketCodec field_56229;
    final /* synthetic */ PacketCodec field_56230;
    final /* synthetic */ Function9 field_56231;
    final /* synthetic */ Function field_56232;
    final /* synthetic */ Function field_56233;
    final /* synthetic */ Function field_56234;
    final /* synthetic */ Function field_56235;
    final /* synthetic */ Function field_56236;
    final /* synthetic */ Function field_56237;
    final /* synthetic */ Function field_56238;
    final /* synthetic */ Function field_56239;
    final /* synthetic */ Function field_56240;

    PacketCodec.7(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, PacketCodec packetCodec5, PacketCodec packetCodec6, PacketCodec packetCodec7, PacketCodec packetCodec8, PacketCodec packetCodec9, Function9 function9, Function function, Function function2, Function function3, Function function4, Function function5, Function function6, Function function7, Function function8, Function function10) {
        this.field_56222 = packetCodec;
        this.field_56223 = packetCodec2;
        this.field_56224 = packetCodec3;
        this.field_56225 = packetCodec4;
        this.field_56226 = packetCodec5;
        this.field_56227 = packetCodec6;
        this.field_56228 = packetCodec7;
        this.field_56229 = packetCodec8;
        this.field_56230 = packetCodec9;
        this.field_56231 = function9;
        this.field_56232 = function;
        this.field_56233 = function2;
        this.field_56234 = function3;
        this.field_56235 = function4;
        this.field_56236 = function5;
        this.field_56237 = function6;
        this.field_56238 = function7;
        this.field_56239 = function8;
        this.field_56240 = function10;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_56222.decode(object);
        Object object3 = this.field_56223.decode(object);
        Object object4 = this.field_56224.decode(object);
        Object object5 = this.field_56225.decode(object);
        Object object6 = this.field_56226.decode(object);
        Object object7 = this.field_56227.decode(object);
        Object object8 = this.field_56228.decode(object);
        Object object9 = this.field_56229.decode(object);
        Object object10 = this.field_56230.decode(object);
        return this.field_56231.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_56222.encode(object, this.field_56232.apply(object2));
        this.field_56223.encode(object, this.field_56233.apply(object2));
        this.field_56224.encode(object, this.field_56234.apply(object2));
        this.field_56225.encode(object, this.field_56235.apply(object2));
        this.field_56226.encode(object, this.field_56236.apply(object2));
        this.field_56227.encode(object, this.field_56237.apply(object2));
        this.field_56228.encode(object, this.field_56238.apply(object2));
        this.field_56229.encode(object, this.field_56239.apply(object2));
        this.field_56230.encode(object, this.field_56240.apply(object2));
    }
}
