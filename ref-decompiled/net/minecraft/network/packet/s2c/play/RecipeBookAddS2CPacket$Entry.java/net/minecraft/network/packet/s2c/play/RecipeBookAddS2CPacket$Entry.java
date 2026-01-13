/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeDisplayEntry;

public record RecipeBookAddS2CPacket.Entry(RecipeDisplayEntry contents, byte flags) {
    public static final byte SHOW_NOTIFICATION = 1;
    public static final byte HIGHLIGHTED = 2;
    public static final PacketCodec<RegistryByteBuf, RecipeBookAddS2CPacket.Entry> PACKET_CODEC = PacketCodec.tuple(RecipeDisplayEntry.PACKET_CODEC, RecipeBookAddS2CPacket.Entry::contents, PacketCodecs.BYTE, RecipeBookAddS2CPacket.Entry::flags, RecipeBookAddS2CPacket.Entry::new);

    public RecipeBookAddS2CPacket.Entry(RecipeDisplayEntry display, boolean showNotification, boolean highlighted) {
        this(display, (byte)((showNotification ? 1 : 0) | (highlighted ? 2 : 0)));
    }

    public boolean shouldShowNotification() {
        return (this.flags & 1) != 0;
    }

    public boolean isHighlighted() {
        return (this.flags & 2) != 0;
    }
}
