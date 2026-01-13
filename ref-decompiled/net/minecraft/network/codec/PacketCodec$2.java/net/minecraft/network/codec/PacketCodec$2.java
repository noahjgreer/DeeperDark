/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function4
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function4;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.2
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_48991;
    final /* synthetic */ PacketCodec field_48992;
    final /* synthetic */ PacketCodec field_48993;
    final /* synthetic */ PacketCodec field_48994;
    final /* synthetic */ Function4 field_48995;
    final /* synthetic */ Function field_48996;
    final /* synthetic */ Function field_48997;
    final /* synthetic */ Function field_48998;
    final /* synthetic */ Function field_48999;

    PacketCodec.2(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, Function4 function4, Function function, Function function2, Function function3, Function function5) {
        this.field_48991 = packetCodec;
        this.field_48992 = packetCodec2;
        this.field_48993 = packetCodec3;
        this.field_48994 = packetCodec4;
        this.field_48995 = function4;
        this.field_48996 = function;
        this.field_48997 = function2;
        this.field_48998 = function3;
        this.field_48999 = function5;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_48991.decode(object);
        Object object3 = this.field_48992.decode(object);
        Object object4 = this.field_48993.decode(object);
        Object object5 = this.field_48994.decode(object);
        return this.field_48995.apply(object2, object3, object4, object5);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_48991.encode(object, this.field_48996.apply(object2));
        this.field_48992.encode(object, this.field_48997.apply(object2));
        this.field_48993.encode(object, this.field_48998.apply(object2));
        this.field_48994.encode(object, this.field_48999.apply(object2));
    }
}
