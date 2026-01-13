/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

class PacketCodecs.11
implements PacketCodec<ByteBuf, Optional<NbtCompound>> {
    PacketCodecs.11() {
    }

    @Override
    public Optional<NbtCompound> decode(ByteBuf byteBuf) {
        return Optional.ofNullable(PacketByteBuf.readNbt(byteBuf));
    }

    @Override
    public void encode(ByteBuf byteBuf, Optional<NbtCompound> optional) {
        PacketByteBuf.writeNbt(byteBuf, optional.orElse(null));
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((ByteBuf)object, (Optional)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((ByteBuf)object);
    }
}
