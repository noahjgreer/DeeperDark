/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen.sync;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.sync.ComponentChangesHash;
import net.minecraft.screen.sync.ItemStackHash;

public record ItemStackHash.Impl(RegistryEntry<Item> item, int count, ComponentChangesHash components) implements ItemStackHash
{
    public static final PacketCodec<RegistryByteBuf, ItemStackHash.Impl> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.registryEntry(RegistryKeys.ITEM), ItemStackHash.Impl::item, PacketCodecs.VAR_INT, ItemStackHash.Impl::count, ComponentChangesHash.PACKET_CODEC, ItemStackHash.Impl::components, ItemStackHash.Impl::new);

    @Override
    public boolean hashEquals(ItemStack stack, ComponentChangesHash.ComponentHasher hasher) {
        if (this.count != stack.getCount()) {
            return false;
        }
        if (!this.item.equals(stack.getRegistryEntry())) {
            return false;
        }
        return this.components.hashEquals(stack.getComponentChanges(), hasher);
    }
}
