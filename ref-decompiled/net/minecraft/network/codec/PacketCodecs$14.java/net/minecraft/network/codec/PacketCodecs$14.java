/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  org.joml.Quaternionfc
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.joml.Quaternionfc;

class PacketCodecs.14
implements PacketCodec<ByteBuf, Quaternionfc> {
    PacketCodecs.14() {
    }

    @Override
    public Quaternionfc decode(ByteBuf byteBuf) {
        return PacketByteBuf.readQuaternionf(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf, Quaternionfc quaternionfc) {
        PacketByteBuf.writeQuaternionf(byteBuf, quaternionfc);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Quaternionfc)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
