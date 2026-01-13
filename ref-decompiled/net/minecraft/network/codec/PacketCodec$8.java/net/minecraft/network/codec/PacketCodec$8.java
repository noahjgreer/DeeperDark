/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function10
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function10;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.8
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_60826;
    final /* synthetic */ PacketCodec field_60827;
    final /* synthetic */ PacketCodec field_60828;
    final /* synthetic */ PacketCodec field_60829;
    final /* synthetic */ PacketCodec field_60830;
    final /* synthetic */ PacketCodec field_60831;
    final /* synthetic */ PacketCodec field_60832;
    final /* synthetic */ PacketCodec field_60833;
    final /* synthetic */ PacketCodec field_60834;
    final /* synthetic */ PacketCodec field_60835;
    final /* synthetic */ Function10 field_60837;
    final /* synthetic */ Function field_60838;
    final /* synthetic */ Function field_60839;
    final /* synthetic */ Function field_60840;
    final /* synthetic */ Function field_60841;
    final /* synthetic */ Function field_60842;
    final /* synthetic */ Function field_60843;
    final /* synthetic */ Function field_60844;
    final /* synthetic */ Function field_60845;
    final /* synthetic */ Function field_60846;
    final /* synthetic */ Function field_60847;

    PacketCodec.8(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, PacketCodec packetCodec4, PacketCodec packetCodec5, PacketCodec packetCodec6, PacketCodec packetCodec7, PacketCodec packetCodec8, PacketCodec packetCodec9, PacketCodec packetCodec10, Function10 function10, Function function, Function function2, Function function3, Function function4, Function function5, Function function6, Function function7, Function function8, Function function9, Function function11) {
        this.field_60826 = packetCodec;
        this.field_60827 = packetCodec2;
        this.field_60828 = packetCodec3;
        this.field_60829 = packetCodec4;
        this.field_60830 = packetCodec5;
        this.field_60831 = packetCodec6;
        this.field_60832 = packetCodec7;
        this.field_60833 = packetCodec8;
        this.field_60834 = packetCodec9;
        this.field_60835 = packetCodec10;
        this.field_60837 = function10;
        this.field_60838 = function;
        this.field_60839 = function2;
        this.field_60840 = function3;
        this.field_60841 = function4;
        this.field_60842 = function5;
        this.field_60843 = function6;
        this.field_60844 = function7;
        this.field_60845 = function8;
        this.field_60846 = function9;
        this.field_60847 = function11;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_60826.decode(object);
        Object object3 = this.field_60827.decode(object);
        Object object4 = this.field_60828.decode(object);
        Object object5 = this.field_60829.decode(object);
        Object object6 = this.field_60830.decode(object);
        Object object7 = this.field_60831.decode(object);
        Object object8 = this.field_60832.decode(object);
        Object object9 = this.field_60833.decode(object);
        Object object10 = this.field_60834.decode(object);
        Object object11 = this.field_60835.decode(object);
        return this.field_60837.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_60826.encode(object, this.field_60838.apply(object2));
        this.field_60827.encode(object, this.field_60839.apply(object2));
        this.field_60828.encode(object, this.field_60840.apply(object2));
        this.field_60829.encode(object, this.field_60841.apply(object2));
        this.field_60830.encode(object, this.field_60842.apply(object2));
        this.field_60831.encode(object, this.field_60843.apply(object2));
        this.field_60832.encode(object, this.field_60844.apply(object2));
        this.field_60833.encode(object, this.field_60845.apply(object2));
        this.field_60834.encode(object, this.field_60846.apply(object2));
        this.field_60835.encode(object, this.field_60847.apply(object2));
    }
}
