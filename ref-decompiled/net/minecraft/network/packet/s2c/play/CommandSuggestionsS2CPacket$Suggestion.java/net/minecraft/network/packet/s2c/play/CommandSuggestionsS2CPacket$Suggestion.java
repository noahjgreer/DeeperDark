/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record CommandSuggestionsS2CPacket.Suggestion(String text, Optional<Text> tooltip) {
    public static final PacketCodec<RegistryByteBuf, CommandSuggestionsS2CPacket.Suggestion> CODEC = PacketCodec.tuple(PacketCodecs.STRING, CommandSuggestionsS2CPacket.Suggestion::text, TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC, CommandSuggestionsS2CPacket.Suggestion::tooltip, CommandSuggestionsS2CPacket.Suggestion::new);
}
