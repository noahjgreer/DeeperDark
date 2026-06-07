package net.noahsarch.deeperdark.payload;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent client→server when the player middle-clicks a block and the target item
 * is not found directly in their inventory. The server searches the player's
 * container items (shulker-like boxes, vaults, bundles) for the target and
 * extracts it if possible.
 */
public record PickFromContainerPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PickFromContainerPayload> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "pick_from_container"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PickFromContainerPayload> CODEC =
        StreamCodec.of(
            (buf, payload) -> buf.writeBlockPos(payload.blockPos()),
            buf -> new PickFromContainerPayload(buf.readBlockPos())
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
