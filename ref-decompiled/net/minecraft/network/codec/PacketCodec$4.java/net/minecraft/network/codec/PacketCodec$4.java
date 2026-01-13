/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function6
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function6;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.4
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_49698;
    final /* synthetic */ PacketCodec field_49699;
    final /* synthetic */ PacketCodec field_49700;
    final /* synthetic */ PacketCodec field_49701;
    final /* synthetic */ PacketCodec field_49702;
    final /* synthetic */ PacketCodec field_49703;
    final /* synthetic */ Function6 field_49704;
    final /* synthetic */ Function field_49705;
    final /* synthetic */ Function field_49706;
    final /* synthetic */ Function field_49707;
    final /* synthetic */ Function field_49708;
    final /* synthetic */ Function field_49709;
    final /* synthetic */ Function field_49710;

    PacketCodec.4(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, PacketCodec packetCodec5, PacketCodec packetCodec6, Function6 function6, Function function, Function function2, Function function3, Function function4, Function function5, Function function7) {
        this.field_49698 = packetCodec;
        this.field_49699 = packetCodec2;
        this.field_49700 = packetCodec3;
        this.field_49701 = packetCodec4;
        this.field_49702 = packetCodec5;
        this.field_49703 = packetCodec6;
        this.field_49704 = function6;
        this.field_49705 = function;
        this.field_49706 = function2;
        this.field_49707 = function3;
        this.field_49708 = function4;
        this.field_49709 = function5;
        this.field_49710 = function7;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_49698.decode(object);
        Object object3 = this.field_49699.decode(object);
        Object object4 = this.field_49700.decode(object);
        Object object5 = this.field_49701.decode(object);
        Object object6 = this.field_49702.decode(object);
        Object object7 = this.field_49703.decode(object);
        return this.field_49704.apply(object2, object3, object4, object5, object6, object7);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_49698.encode(object, this.field_49705.apply(object2));
        this.field_49699.encode(object, this.field_49706.apply(object2));
        this.field_49700.encode(object, this.field_49707.apply(object2));
        this.field_49701.encode(object, this.field_49708.apply(object2));
        this.field_49702.encode(object, this.field_49709.apply(object2));
        this.field_49703.encode(object, this.field_49710.apply(object2));
    }
}
