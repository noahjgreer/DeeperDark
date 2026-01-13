/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.DecoderException
 *  io.netty.handler.codec.EncoderException
 */
package net.minecraft.item;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

class ItemStack.2
implements PacketCodec<RegistryByteBuf, ItemStack> {
    ItemStack.2() {
    }

    @Override
    public ItemStack decode(RegistryByteBuf registryByteBuf) {
        ItemStack itemStack = (ItemStack)OPTIONAL_PACKET_CODEC.decode(registryByteBuf);
        if (itemStack.isEmpty()) {
            throw new DecoderException("Empty ItemStack not allowed");
        }
        return itemStack;
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            throw new EncoderException("Empty ItemStack not allowed");
        }
        OPTIONAL_PACKET_CODEC.encode(registryByteBuf, itemStack);
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
