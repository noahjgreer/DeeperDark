package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent client→server when the player presses the "open container item" key
 * while hovering an inventory slot that holds a shulker box or box.
 */
public record OpenContainerItemPayload(int slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenContainerItemPayload> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "open_container_item"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenContainerItemPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, OpenContainerItemPayload::slot,
        OpenContainerItemPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
