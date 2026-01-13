/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.data;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

class TrackedDataHandlerRegistry.1
implements TrackedDataHandler<ItemStack> {
    TrackedDataHandlerRegistry.1() {
    }

    @Override
    public PacketCodec<? super RegistryByteBuf, ItemStack> codec() {
        return ItemStack.OPTIONAL_PACKET_CODEC;
    }

    @Override
    public ItemStack copy(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public /* synthetic */ Object copy(Object object) {
        return this.copy((ItemStack)object);
    }
}
