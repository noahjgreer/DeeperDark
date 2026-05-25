package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent server→client to sync the void fog enabled state.
 * Sent on player join and whenever an operator changes void_fog_enabled via /dd config.
 */
public record VoidFogSyncPacket(boolean enabled) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VoidFogSyncPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "void_fog_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, VoidFogSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, VoidFogSyncPacket::enabled,
            VoidFogSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
