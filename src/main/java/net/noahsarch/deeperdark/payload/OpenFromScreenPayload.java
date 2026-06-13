package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent server→client after opening a container from the inventory screen.
 * The client uses the containerId to recognize that the current screen should
 * return to inventory when closed, rather than exiting to the game world.
 */
public record OpenFromScreenPayload(int containerId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenFromScreenPayload> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "open_from_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenFromScreenPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, OpenFromScreenPayload::containerId,
        OpenFromScreenPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
