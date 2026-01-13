/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Function3
 */
package net.minecraft.network.codec;

import com.mojang.datafixers.util.Function3;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodec.19
implements PacketCodec<B, C> {
    final /* synthetic */ PacketCodec field_48603;
    final /* synthetic */ PacketCodec field_48604;
    final /* synthetic */ PacketCodec field_48605;
    final /* synthetic */ Function3 field_48606;
    final /* synthetic */ Function field_48607;
    final /* synthetic */ Function field_48608;
    final /* synthetic */ Function field_48609;

    PacketCodec.19(PacketCodec packetCodec, PacketCodec packetCodec2, PacketCodec packetCodec3, Function3 function3, Function function, Function function2, Function function4) {
        this.field_48603 = packetCodec;
        this.field_48604 = packetCodec2;
        this.field_48605 = packetCodec3;
        this.field_48606 = function3;
        this.field_48607 = function;
        this.field_48608 = function2;
        this.field_48609 = function4;
    }

    @Override
    public C decode(B object) {
        Object object2 = this.field_48603.decode(object);
        Object object3 = this.field_48604.decode(object);
        Object object4 = this.field_48605.decode(object);
        return this.field_48606.apply(object2, object3, object4);
    }

    @Override
    public void encode(B object, C object2) {
        this.field_48603.encode(object, this.field_48607.apply(object2));
        this.field_48604.encode(object, this.field_48608.apply(object2));
        this.field_48605.encode(object, this.field_48609.apply(object2));
    }
}
