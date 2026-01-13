/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.DecoderException
 */
package net.minecraft.item;

import io.netty.handler.codec.DecoderException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.NullOps;

static class ItemStack.3
implements PacketCodec<RegistryByteBuf, ItemStack> {
    final /* synthetic */ PacketCodec field_51399;

    ItemStack.3(PacketCodec packetCodec) {
        this.field_51399 = packetCodec;
    }

    @Override
    public ItemStack decode(RegistryByteBuf registryByteBuf) {
        ItemStack itemStack = (ItemStack)this.field_51399.decode(registryByteBuf);
        if (!itemStack.isEmpty()) {
            RegistryOps<Unit> registryOps = registryByteBuf.getRegistryManager().getOps(NullOps.INSTANCE);
            CODEC.encodeStart(registryOps, (Object)itemStack).getOrThrow(DecoderException::new);
        }
        return itemStack;
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, ItemStack itemStack) {
        this.field_51399.encode(registryByteBuf, itemStack);
    }

    @Override
    public /* synthetic */ void encode(Object object, Object object2) {
        this.encode((RegistryByteBuf)((Object)object), (ItemStack)object2);
    }

    @Override
    public /* synthetic */ Object decode(Object object) {
        return this.decode((RegistryByteBuf)((Object)object));
    }
}
