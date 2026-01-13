/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function8
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function8;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.6
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_54929;
    final /* synthetic */ PacketCodec field_54930;
    final /* synthetic */ PacketCodec field_54931;
    final /* synthetic */ PacketCodec field_54932;
    final /* synthetic */ PacketCodec field_54933;
    final /* synthetic */ PacketCodec field_54934;
    final /* synthetic */ PacketCodec field_54935;
    final /* synthetic */ PacketCodec field_54936;
    final /* synthetic */ Function8 field_54937;
    final /* synthetic */ Function field_54938;
    final /* synthetic */ Function field_54939;
    final /* synthetic */ Function field_54940;
    final /* synthetic */ Function field_54941;
    final /* synthetic */ Function field_54942;
    final /* synthetic */ Function field_54943;
    final /* synthetic */ Function field_54944;
    final /* synthetic */ Function field_54945;

    PacketCodec.6(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, PacketCodec packetCodec5, PacketCodec packetCodec6, PacketCodec packetCodec7, PacketCodec packetCodec8, Function8 function8, Function function, Function function2, Function function3, Function function4, Function function5, Function function6, Function function7, Function function9) {
        this.field_54929 = packetCodec;
        this.field_54930 = packetCodec2;
        this.field_54931 = packetCodec3;
        this.field_54932 = packetCodec4;
        this.field_54933 = packetCodec5;
        this.field_54934 = packetCodec6;
        this.field_54935 = packetCodec7;
        this.field_54936 = packetCodec8;
        this.field_54937 = function8;
        this.field_54938 = function;
        this.field_54939 = function2;
        this.field_54940 = function3;
        this.field_54941 = function4;
        this.field_54942 = function5;
        this.field_54943 = function6;
        this.field_54944 = function7;
        this.field_54945 = function9;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_54929.decode(object);
        Object object3 = this.field_54930.decode(object);
        Object object4 = this.field_54931.decode(object);
        Object object5 = this.field_54932.decode(object);
        Object object6 = this.field_54933.decode(object);
        Object object7 = this.field_54934.decode(object);
        Object object8 = this.field_54935.decode(object);
        Object object9 = this.field_54936.decode(object);
        return this.field_54937.apply(object2, object3, object4, object5, object6, object7, object8, object9);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_54929.encode(object, this.field_54938.apply(object2));
        this.field_54930.encode(object, this.field_54939.apply(object2));
        this.field_54931.encode(object, this.field_54940.apply(object2));
        this.field_54932.encode(object, this.field_54941.apply(object2));
        this.field_54933.encode(object, this.field_54942.apply(object2));
        this.field_54934.encode(object, this.field_54943.apply(object2));
        this.field_54935.encode(object, this.field_54944.apply(object2));
        this.field_54936.encode(object, this.field_54945.apply(object2));
    }
}
