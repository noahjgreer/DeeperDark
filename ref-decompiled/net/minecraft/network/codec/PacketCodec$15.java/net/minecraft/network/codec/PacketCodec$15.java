/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import net.minecraft.network.codec.PacketCodec;

class PacketCodec.15
implements PacketCodec<O, V> {
    final /* synthetic */ Function field_48590;

    PacketCodec.15(Function function) {
        this.field_48590 = function;
    }

    @Override
    public V decode(O byteBuf) {
        Object object = this.field_48590.apply(byteBuf);
        return PacketCodec.this.decode(object);
    }

    @Override
    public void encode(O byteBuf, V object) {
        Object object2 = this.field_48590.apply(byteBuf);
        PacketCodec.this.encode(object2, object);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((O)((ByteBuf)object), (V)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((O)((ByteBuf)object));
    }
}
