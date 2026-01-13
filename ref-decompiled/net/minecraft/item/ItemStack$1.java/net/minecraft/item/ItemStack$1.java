/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.component.ComponentChanges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;

static class ItemStack.1
implements PacketCodec<RegistryByteBuf, ItemStack> {
    final /* synthetic */ PacketCodec field_58142;

    ItemStack.1(PacketCodec packetCodec) {
        this.field_58142 = packetCodec;
    }

    @Override
    public ItemStack decode(RegistryByteBuf registryByteBuf) {
        int i = registryByteBuf.readVarInt();
        if (i <= 0) {
            return EMPTY;
        }
        RegistryEntry registryEntry = (RegistryEntry)Item.ENTRY_PACKET_CODEC.decode(registryByteBuf);
        ComponentChanges componentChanges = (ComponentChanges)this.field_58142.decode(registryByteBuf);
        return new ItemStack(registryEntry, i, componentChanges);
    }

    @Override
    public void encode(RegistryByteBuf registryByteBuf, ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            registryByteBuf.writeVarInt(0);
            return;
        }
        registryByteBuf.writeVarInt(itemStack.getCount());
        Item.ENTRY_PACKET_CODEC.encode(registryByteBuf, itemStack.getRegistryEntry());
        this.field_58142.encode(registryByteBuf, itemStack.components.getChanges());
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
