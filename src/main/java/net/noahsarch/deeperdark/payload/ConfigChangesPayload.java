package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/** Sent client→server when the config menu's Done is pressed: only the values that changed. */
public record ConfigChangesPayload(Map<String, String> changes) implements CustomPacketPayload {
    public static final int MAX_CHANGES = 1024;

    public static final CustomPacketPayload.Type<ConfigChangesPayload> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "config_changes"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigChangesPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.stringUtf8(256), ByteBufCodecs.stringUtf8(256), MAX_CHANGES), ConfigChangesPayload::changes,
            ConfigChangesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
