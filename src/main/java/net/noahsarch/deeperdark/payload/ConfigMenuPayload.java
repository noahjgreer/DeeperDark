package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Sent server→client by "/dd" to open the config menu. Holds only the values this player may
 * edit, keyed as described in {@link net.noahsarch.deeperdark.config.ConfigMenu}.
 */
public record ConfigMenuPayload(boolean operator, Map<String, String> values) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConfigMenuPayload> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "config_menu"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigMenuPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ConfigMenuPayload::operator,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), ConfigMenuPayload::values,
            ConfigMenuPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
