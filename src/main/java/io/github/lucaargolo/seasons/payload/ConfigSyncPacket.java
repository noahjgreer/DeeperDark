package io.github.lucaargolo.seasons.payload;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ConfigSyncPacket(String config) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConfigSyncPacket> ID = new CustomPacketPayload.Type<>(FabricSeasons.identifier("config_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ConfigSyncPacket::config,
        ConfigSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
