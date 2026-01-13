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
import java.util.function.Supplier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtEnd;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodecs.8
implements PacketCodec<ByteBuf, NbtElement> {
    final /* synthetic */ Supplier field_61048;

    PacketCodecs.8(Supplier supplier) {
        this.field_61048 = supplier;
    }

    @Override
    public NbtElement decode(ByteBuf byteBuf) {
        NbtElement nbtElement = PacketByteBuf.readNbt(byteBuf, (NbtSizeTracker)this.field_61048.get());
        if (nbtElement == null) {
            throw new DecoderException("Expected non-null compound tag");
        }
        return nbtElement;
    }

    @Override
    public void encode(ByteBuf byteBuf, NbtElement nbtElement) {
        if (nbtElement == NbtEnd.INSTANCE) {
            throw new EncoderException("Expected non-null compound tag");
        }
        PacketByteBuf.writeNbt(byteBuf, nbtElement);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (NbtElement)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
