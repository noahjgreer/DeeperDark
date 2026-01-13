/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.entity.data;

import io.netty.buffer.ByteBuf;
import java.util.OptionalInt;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;

class TrackedDataHandlerRegistry.3
implements PacketCodec<ByteBuf, OptionalInt> {
    TrackedDataHandlerRegistry.3() {
    }

    @Override
    public OptionalInt decode(ByteBuf byteBuf) {
        int i = VarInts.read(byteBuf);
        return i == 0 ? OptionalInt.empty() : OptionalInt.of(i - 1);
    }

    @Override
    public void encode(ByteBuf byteBuf, OptionalInt optionalInt) {
        VarInts.write(byteBuf, optionalInt.orElse(-1) + 1);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (OptionalInt)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
