package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent server→client to tell the leashed player's own client about their leash state.
 * holderEntityId == -1 means the leash was removed.
 * Needed because the vanilla ClientboundSetEntityLinkPacket handler only processes Mob entities,
 * so the leashed player's client never sees the packet through normal broadcast.
 */
public record PlayerLeashPacket(int leashedEntityId, int holderEntityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlayerLeashPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "player_leash"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerLeashPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PlayerLeashPacket::leashedEntityId,
            ByteBufCodecs.INT, PlayerLeashPacket::holderEntityId,
            PlayerLeashPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
