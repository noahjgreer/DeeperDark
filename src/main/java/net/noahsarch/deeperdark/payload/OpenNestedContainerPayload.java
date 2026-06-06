package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent client→server when the player presses the "open container item" key while
 * hovering a slot inside an already-open container (e.g. a shulker box inside an
 * open ender chest). The server looks up the slot in the player's active menu.
 */
public record OpenNestedContainerPayload(int menuSlotIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenNestedContainerPayload> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "open_nested_container"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenNestedContainerPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, OpenNestedContainerPayload::menuSlotIndex,
        OpenNestedContainerPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
