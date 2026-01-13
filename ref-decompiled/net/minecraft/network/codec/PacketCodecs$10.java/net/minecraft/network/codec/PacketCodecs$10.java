/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 */
package net.minecraft.network.codec;

import com.mojang.serialization.Codec;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;

static class PacketCodecs.10
implements PacketCodec<RegistryByteBuf, T> {
    final /* synthetic */ PacketCodec field_60519;
    final /* synthetic */ Codec field_60520;

    PacketCodecs.10(PacketCodec packetCodec, Codec codec) {
        this.field_60519 = packetCodec;
        this.field_60520 = codec;
    }

    @Override
    public T decode(RegistryByteBuf registryByteBuf) {
        NbtElement nbtElement = (NbtElement)this.field_60519.decode(registryByteBuf);
        RegistryOps<NbtElement> registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
        return this.field_60520.parse(registryOps, (Object)nbtElement).getOrThrow(error -> new DecoderException("Failed to decode: " + error + " " + String.valueOf(nbtElement)));
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, T object) {
        RegistryOps<NbtElement> registryOps = registryByteBuf.getRegistryManager().getOps(NbtOps.INSTANCE);
        NbtElement nbtElement = (NbtElement)this.field_60520.encodeStart(registryOps, object).getOrThrow(error -> new EncoderException("Failed to encode: " + error + " " + String.valueOf(object)));
        this.field_60519.encode(registryByteBuf, nbtElement);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((RegistryByteBuf)((Object)object), object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((RegistryByteBuf)((Object)object));
    }
}
