package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Sent client→server as the player types in an open crate's search box. */
public record CrateSearchPayload(int containerId, String query, int generation) implements CustomPacketPayload {
    public static final int MAX_LENGTH = 50;
    public static final CustomPacketPayload.Type<CrateSearchPayload> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "crate_search"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CrateSearchPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, CrateSearchPayload::containerId,
        ByteBufCodecs.stringUtf8(MAX_LENGTH), CrateSearchPayload::query,
        ByteBufCodecs.VAR_INT, CrateSearchPayload::generation,
        CrateSearchPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
