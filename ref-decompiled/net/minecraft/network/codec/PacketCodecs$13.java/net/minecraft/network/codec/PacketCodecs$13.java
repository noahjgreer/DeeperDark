/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  org.joml.Vector3fc
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.joml.Vector3fc;

class PacketCodecs.13
implements PacketCodec<ByteBuf, Vector3fc> {
    PacketCodecs.13() {
    }

    @Override
    public Vector3fc decode(ByteBuf byteBuf) {
        return PacketByteBuf.readVector3f(byteBuf);
    }

    @Override
    public void encode(ByteBuf byteBuf, Vector3fc vector3fc) {
        PacketByteBuf.writeVector3f(byteBuf, vector3fc);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Vector3fc)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
