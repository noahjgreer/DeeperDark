package io.github.lucaargolo.seasons.payload;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SeasonTimeSyncPacket(long overworldTime, long gameTime) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SeasonTimeSyncPacket> ID =
            new CustomPacketPayload.Type<>(FabricSeasons.identifier("season_time_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SeasonTimeSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, SeasonTimeSyncPacket::overworldTime,
            ByteBufCodecs.VAR_LONG, SeasonTimeSyncPacket::gameTime,
            SeasonTimeSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
