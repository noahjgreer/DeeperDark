/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.function.BiFunction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

static class PacketCodecs.20
implements PacketCodec<B, V> {
    final /* synthetic */ int field_60497;
    final /* synthetic */ BiFunction field_60498;
    final /* synthetic */ PacketCodec field_60499;

    PacketCodecs.20(int i, BiFunction biFunction, PacketCodec packetCodec) {
        this.field_60497 = i;
        this.field_60498 = biFunction;
        this.field_60499 = packetCodec;
    }

    @Override
    public V decode(B byteBuf) {
        int i = VarInts.read(byteBuf);
        if (i > this.field_60497) {
            throw new DecoderException("Buffer size " + i + " is larger than allowed limit of " + this.field_60497);
        }
        int j = byteBuf.readerIndex();
        ByteBuf byteBuf2 = (ByteBuf)this.field_60498.apply(byteBuf, byteBuf.slice(j, i));
        byteBuf.readerIndex(j + i);
        return this.field_60499.decode(byteBuf2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void encode(B byteBuf, V object) {
        ByteBuf byteBuf2 = (ByteBuf)this.field_60498.apply(byteBuf, byteBuf.alloc().buffer());
        try {
            this.field_60499.encode(byteBuf2, object);
            int i = byteBuf2.readableBytes();
            if (i > this.field_60497) {
                throw new EncoderException("Buffer size " + i + " is  larger than allowed limit of " + this.field_60497);
            }
            VarInts.write(byteBuf, i);
            byteBuf.writeBytes(byteBuf2);
        }
        finally {
            byteBuf2.release();
        }
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((B)((ByteBuf)object), (V)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((B)((ByteBuf)object));
    }
}
