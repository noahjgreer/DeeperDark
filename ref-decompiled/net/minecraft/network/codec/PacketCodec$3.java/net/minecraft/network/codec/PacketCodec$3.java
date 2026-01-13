/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function5
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function5;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.3
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_49000;
    final /* synthetic */ PacketCodec field_49001;
    final /* synthetic */ PacketCodec field_49002;
    final /* synthetic */ PacketCodec field_49003;
    final /* synthetic */ PacketCodec field_49004;
    final /* synthetic */ Function5 field_49005;
    final /* synthetic */ Function field_49006;
    final /* synthetic */ Function field_49007;
    final /* synthetic */ Function field_49008;
    final /* synthetic */ Function field_49009;
    final /* synthetic */ Function field_49010;

    PacketCodec.3(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, PacketCodec packetCodec5, Function5 function5, Function function, Function function2, Function function3, Function function4, Function function6) {
        this.field_49000 = packetCodec;
        this.field_49001 = packetCodec2;
        this.field_49002 = packetCodec3;
        this.field_49003 = packetCodec4;
        this.field_49004 = packetCodec5;
        this.field_49005 = function5;
        this.field_49006 = function;
        this.field_49007 = function2;
        this.field_49008 = function3;
        this.field_49009 = function4;
        this.field_49010 = function6;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_49000.decode(object);
        Object object3 = this.field_49001.decode(object);
        Object object4 = this.field_49002.decode(object);
        Object object5 = this.field_49003.decode(object);
        Object object6 = this.field_49004.decode(object);
        return this.field_49005.apply(object2, object3, object4, object5, object6);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_49000.encode(object, this.field_49006.apply(object2));
        this.field_49001.encode(object, this.field_49007.apply(object2));
        this.field_49002.encode(object, this.field_49008.apply(object2));
        this.field_49003.encode(object, this.field_49009.apply(object2));
        this.field_49004.encode(object, this.field_49010.apply(object2));
    }
}
