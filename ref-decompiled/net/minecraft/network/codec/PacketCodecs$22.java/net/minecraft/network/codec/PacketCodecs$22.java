/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.codec;

import java.util.function.Function;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.VarInts;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.collection.IndexedIterable;

static class PacketCodecs.22
implements PacketCodec<RegistryByteBuf, R> {
    final /* synthetic */ Function field_60502;
    final /* synthetic */ RegistryKey field_60503;

    PacketCodecs.22(Function function, RegistryKey registryKey) {
        this.field_60502 = function;
        this.field_60503 = registryKey;
    }

    private IndexedIterable<R> getRegistryOrThrow(RegistryByteBuf buf) {
        return (IndexedIterable)this.field_60502.apply(buf.getRegistryManager().getOrThrow(this.field_60503));
    }

    @Override
    public R decode(RegistryByteBuf registryByteBuf) {
        int i = VarInts.read(registryByteBuf);
        return this.getRegistryOrThrow(registryByteBuf).getOrThrow(i);
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, R object) {
        int i = this.getRegistryOrThrow(registryByteBuf).getRawIdOrThrow(object);
        VarInts.write(registryByteBuf, i);
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
