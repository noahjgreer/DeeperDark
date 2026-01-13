/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.util.math;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.EulerAngle;

class EulerAngle.1
implements PacketCodec<ByteBuf, EulerAngle> {
    EulerAngle.1() {
    }

    @Override
    public EulerAngle decode(ByteBuf byteBuf) {
        return new EulerAngle(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat());
    }

    @Override
    public void encode(ByteBuf byteBuf, EulerAngle eulerAngle) {
        byteBuf.writeFloat(eulerAngle.pitch);
        byteBuf.writeFloat(eulerAngle.yaw);
        byteBuf.writeFloat(eulerAngle.roll);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (EulerAngle)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
