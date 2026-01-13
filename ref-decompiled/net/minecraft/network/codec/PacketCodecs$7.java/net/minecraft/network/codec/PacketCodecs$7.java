/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

static class PacketCodecs.7
implements PacketCodec<ByteBuf, Optional<NbtElement>> {
    final /* synthetic */ Supplier field_57070;

    PacketCodecs.7(Supplier supplier) {
        this.field_57070 = supplier;
    }

    @Override
    public Optional<NbtElement> decode(ByteBuf byteBuf) {
        return Optional.ofNullable(PacketByteBuf.readNbt(byteBuf, (NbtSizeTracker)this.field_57070.get()));
    }

    @Override
    public void encode(ByteBuf byteBuf, Optional<NbtElement> optional) {
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
